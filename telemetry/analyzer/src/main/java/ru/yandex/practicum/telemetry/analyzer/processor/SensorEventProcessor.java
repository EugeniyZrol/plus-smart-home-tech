package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaProperties;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioProcessorService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorEventProcessor implements Runnable {

    private final KafkaConsumer<String, SensorEventAvro> sensorEventConsumer;
    private final ScenarioProcessorService scenarioProcessorService;
    private final KafkaProperties kafkaProperties;

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown...");
            sensorEventConsumer.wakeup();
        }));

        try {
            sensorEventConsumer.subscribe(Collections.singletonList(kafkaProperties.getSensorsTopic()));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records =
                        sensorEventConsumer.poll(Duration.ofMillis(kafkaProperties.getPollTimeoutMs()));

                if (records.isEmpty()) {
                    continue;
                }

                records.forEach(record -> {
                    SensorEventAvro sensorEvent = record.value();
                    try {
                        scenarioProcessorService.processSensorEvent(sensorEvent);
                    } catch (Exception e) {
                        log.error("Ошибка обработки события сенсора: {}", sensorEvent.getId(), e);
                    }
                });
            }

        } catch (WakeupException e) {
            log.info("Shutdown завершен");
        } catch (Exception e) {
            log.error("Ошибка обработки событий сенсора", e);
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            if (sensorEventConsumer != null) {
                sensorEventConsumer.close();
            }
        } catch (Exception e) {
            log.error("Ошибка при cleanup", e);
        }
    }
}