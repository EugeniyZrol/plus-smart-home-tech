package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaConfigProperties;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioExecutionService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final ScenarioExecutionService scenarioExecutionService;
    private final KafkaConfigProperties kafkaConfigProperties;

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown SnapshotProcessor...");
            snapshotConsumer.wakeup();
        }));

        try {
            snapshotConsumer.subscribe(Collections.singletonList(
                    kafkaConfigProperties.getConsumer().getSnapshotsTopic()
            ));
            log.info("SnapshotProcessor запущен. Подписан на топик: {}",
                    kafkaConfigProperties.getConsumer().getSnapshotsTopic());

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        snapshotConsumer.poll(kafkaConfigProperties.getConsumer().getPollTimeout());

                if (records.isEmpty()) {
                    continue;
                }

                records.forEach(record -> {
                    SensorsSnapshotAvro snapshot = record.value();
                    try {
                        scenarioExecutionService.executeScenariosForSnapshot(snapshot);
                    } catch (Exception e) {
                        log.error("Ошибка выполнения сценариев для хаба: {}", snapshot.getHubId(), e);
                    }
                });
            }

        } catch (WakeupException e) {
            log.info("SnapshotProcessor shutdown завершен");
        } catch (Exception e) {
            log.error("Ошибка обработки снапшотов", e);
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            if (snapshotConsumer != null) {
                snapshotConsumer.close();
            }
        } catch (Exception e) {
            log.error("Ошибка при cleanup SnapshotProcessor", e);
        }
    }
}