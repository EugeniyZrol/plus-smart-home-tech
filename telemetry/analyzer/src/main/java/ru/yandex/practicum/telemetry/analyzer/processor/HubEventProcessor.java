package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioStorageService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> hubEventConsumer;
    private final ScenarioStorageService scenarioStorageService;

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown HubEventProcessor...");
            hubEventConsumer.wakeup();
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                log.error("Ошибка во время shutdown", e);
            }
        }));

        try {
            String hubEventsTopic = "telemetry.hubs.v1";
            hubEventConsumer.subscribe(Collections.singletonList(hubEventsTopic));
            log.info("HubEventProcessor запущен. Подписан на топик: {}", hubEventsTopic);

            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        hubEventConsumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    continue;
                }

                log.debug("Получено {} событий хаба", records.count());

                records.forEach(record -> {
                    HubEventAvro hubEvent = record.value();
                    try {
                        processHubEvent(hubEvent);
                    } catch (Exception e) {
                        log.error("Ошибка обработки события хаба: {}", hubEvent.getHubId(), e);
                    }
                });

                hubEventConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("WakeupException, завершение работы HubEventProcessor...");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий хаба", e);
        } finally {
            cleanup();
        }
    }

    private void processHubEvent(HubEventAvro hubEvent) {
        String hubId = hubEvent.getHubId();

        log.debug("Обработка события хаба: {}, тип payload: {}",
                hubId, hubEvent.getPayload().getClass().getSimpleName());

        try {
            switch (hubEvent.getPayload()) {
                case DeviceAddedEventAvro deviceAddedEventAvro -> {
                    scenarioStorageService.addDevice(hubId, deviceAddedEventAvro);
                    log.info("Добавлено устройство для хаба: {}", hubId);
                }
                case DeviceRemovedEventAvro deviceRemovedEventAvro -> {
                    scenarioStorageService.removeDevice(hubId, deviceRemovedEventAvro);
                    log.info("Удалено устройство для хаба: {}", hubId);
                }
                case ScenarioAddedEventAvro scenarioAddedEventAvro -> {
                    scenarioStorageService.addScenario(hubId, scenarioAddedEventAvro);
                    log.info("Добавлен сценарий для хаба: {}", hubId);
                }
                case ScenarioRemovedEventAvro scenarioRemovedEventAvro -> {
                    scenarioStorageService.removeScenario(hubId, scenarioRemovedEventAvro);
                    log.info("Удален сценарий для хаба: {}", hubId);
                }
                case null, default -> log.warn("Неизвестный тип события хаба: {}",
                        hubEvent.getPayload().getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события хаба {}: {}", hubId, e.getMessage(), e);
        }
    }

    private void cleanup() {
        try {
            log.info("Фиксация оффсетов HubEventProcessor...");
            if (hubEventConsumer != null) {
                hubEventConsumer.commitSync();
                hubEventConsumer.close();
            }
        } catch (Exception e) {
            log.error("Ошибка во время cleanup HubEventProcessor", e);
        }
    }
}