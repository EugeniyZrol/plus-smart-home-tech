package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioProcessorService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorEventProcessor implements Runnable {

    private final KafkaConsumer<String, SensorEventAvro> sensorEventConsumer;
    private final ScenarioProcessorService scenarioProcessorService;

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown SensorEventProcessor...");
            sensorEventConsumer.wakeup();
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                log.error("Ошибка во время shutdown", e);
            }
        }));

        try {
            String sensorEventsTopic = "telemetry.sensors.v1";
            sensorEventConsumer.subscribe(Collections.singletonList(sensorEventsTopic));
            log.info("SensorEventProcessor запущен. Подписан на топик: {}", sensorEventsTopic);

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records =
                        sensorEventConsumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    continue;
                }

                log.debug("Получено {} событий от датчиков", records.count());

                records.forEach(record -> {
                    SensorEventAvro sensorEvent = record.value();
                    try {
                        processSensorEvent(sensorEvent);
                    } catch (Exception e) {
                        log.error("Ошибка обработки события датчика: {}", sensorEvent.getId(), e);
                    }
                });

                sensorEventConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("WakeupException, завершение работы SensorEventProcessor...");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий датчиков", e);
        } finally {
            cleanup();
        }
    }

    private void processSensorEvent(SensorEventAvro sensorEvent) {
        String eventType = getEventType(sensorEvent);
        log.debug("Обработка события от датчика {} для хаба {} (тип: {})",
                sensorEvent.getId(), sensorEvent.getHubId(), eventType);

        scenarioProcessorService.processSensorEvent(sensorEvent);
    }

    private String getEventType(SensorEventAvro sensorEvent) {
        Object payload = sensorEvent.getPayload();

        switch (payload) {
            case null -> {
                return "UNKNOWN_EVENT";
            }
            case ClimateSensorAvro climateSensorAvro -> {
                return "CLIMATE_SENSOR_EVENT";
            }
            case LightSensorAvro lightSensorAvro -> {
                return "LIGHT_SENSOR_EVENT";
            }
            case MotionSensorAvro motionSensorAvro -> {
                return "MOTION_SENSOR_EVENT";
            }
            case SwitchSensorAvro switchSensorAvro -> {
                return "SWITCH_SENSOR_EVENT";
            }
            case TemperatureSensorAvro temperatureSensorAvro -> {
                return "TEMPERATURE_SENSOR_EVENT";
            }
            default -> {
                log.warn("Неизвестный тип события: {}", payload.getClass().getSimpleName());
                return "UNKNOWN_EVENT";
            }
        }

    }

    private void cleanup() {
        try {
            log.info("Фиксация оффсетов SensorEventProcessor...");
            if (sensorEventConsumer != null) {
                sensorEventConsumer.commitSync();
                sensorEventConsumer.close();
            }
        } catch (Exception e) {
            log.error("Ошибка во время cleanup SensorEventProcessor", e);
        }
    }
}