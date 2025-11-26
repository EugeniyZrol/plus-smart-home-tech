package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaProperties;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioExecutionService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final ScenarioExecutionService scenarioExecutionService;
    private final KafkaProperties kafkaProperties;

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown...");
            snapshotConsumer.wakeup();
        }));

        try {
            snapshotConsumer.subscribe(Collections.singletonList(kafkaProperties.getSnapshotsTopic()));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        snapshotConsumer.poll(Duration.ofMillis(kafkaProperties.getPollTimeoutMs()));

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
            log.info("Shutdown завершен");
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
            log.error("Ошибка при cleanup", e);
        }
    }
}