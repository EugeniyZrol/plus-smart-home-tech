package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioExecutionService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final ScenarioExecutionService scenarioExecutionService;

    public void start() {
        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown SnapshotProcessor...");
            snapshotConsumer.wakeup();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                log.error("Ошибка во время shutdown", e);
            }
        }));

        try {
            String snapshotsTopic = "telemetry.snapshots.v1";
            snapshotConsumer.subscribe(Collections.singletonList(snapshotsTopic));
            log.info("SnapshotProcessor запущен. Подписан на топик: {}", snapshotsTopic);

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        snapshotConsumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    continue;
                }

                log.debug("Получено {} снапшотов", records.count());

                records.forEach(record -> {
                    SensorsSnapshotAvro snapshot = record.value();
                    try {
                        scenarioExecutionService.executeScenariosForSnapshot(snapshot);
                    } catch (Exception e) {
                        log.error("Ошибка выполнения сценариев для хаба: {}", snapshot.getHubId(), e);
                    }
                });

                snapshotConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("WakeupException, завершение работы SnapshotProcessor...");
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            log.info("Фиксация оффсетов SnapshotProcessor...");
            if (snapshotConsumer != null) {
                snapshotConsumer.commitSync();
                snapshotConsumer.close();
            }
        } catch (Exception e) {
            log.error("Ошибка во время cleanup SnapshotProcessor", e);
        }
    }
}