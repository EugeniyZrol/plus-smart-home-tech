package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaConfigProperties;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioStorageService;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> hubEventConsumer;
    private final ScenarioStorageService scenarioStorageService;
    private final KafkaConfigProperties kafkaConfigProperties;

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown HubEventProcessor...");
            hubEventConsumer.wakeup();
        }));

        try {
            hubEventConsumer.subscribe(Collections.singletonList(
                    kafkaConfigProperties.getConsumer().getHubEventsTopic()
            ));
            log.info("HubEventProcessor запущен. Подписан на топик: {}",
                    kafkaConfigProperties.getConsumer().getHubEventsTopic());

            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        hubEventConsumer.poll(kafkaConfigProperties.getConsumer().getPollTimeout());

                if (records.isEmpty()) {
                    continue;
                }

                records.forEach(record -> {
                    HubEventAvro hubEvent = record.value();
                    try {
                        processHubEvent(hubEvent);
                    } catch (Exception e) {
                        log.error("Ошибка обработки события хаба: {}", hubEvent.getHubId(), e);
                    }
                });
            }

        } catch (WakeupException e) {
            log.info("HubEventProcessor shutdown завершен");
        } catch (Exception e) {
            log.error("Ошибка обработки событий хаба", e);
        } finally {
            cleanup();
        }
    }

    private void processHubEvent(HubEventAvro hubEvent) {
        String hubId = hubEvent.getHubId();

        switch (hubEvent.getPayload()) {
            case DeviceAddedEventAvro deviceAddedEvent -> {
                scenarioStorageService.addDevice(hubId, deviceAddedEvent);
                log.debug("Устройство добавлено для хаба: {}", hubId);
            }
            case DeviceRemovedEventAvro deviceRemovedEvent -> {
                scenarioStorageService.removeDevice(hubId, deviceRemovedEvent);
                log.debug("Устройство удалено для хаба: {}", hubId);
            }
            case ScenarioAddedEventAvro scenarioAddedEvent -> {
                scenarioStorageService.addScenario(hubId, scenarioAddedEvent);
                log.debug("Сценарий добавлен для хаба: {}", hubId);
            }
            case ScenarioRemovedEventAvro scenarioRemovedEvent -> {
                scenarioStorageService.removeScenario(hubId, scenarioRemovedEvent);
                log.debug("Сценарий удален для хаба: {}", hubId);
            }
            default -> log.warn("Неизвестный тип события хаба: {}",
                    hubEvent.getPayload().getClass().getSimpleName());
        }
    }

    private void cleanup() {
        try {
            if (hubEventConsumer != null) {
                hubEventConsumer.close();
            }
        } catch (Exception e) {
            log.error("Ошибка при cleanup HubEventProcessor", e);
        }
    }
}