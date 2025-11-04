package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaConsumer<String, SensorEventAvro> kafkaConsumer;
    private final Producer<String, SensorsSnapshotAvro> kafkaProducer;
    private final AggregatorService aggregatorService;

    public void start() {
        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown...");
            kafkaConsumer.wakeup();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                log.error("Ошибка во время shutdown", e);
            }
        }));

        try {
            String sensorEventsTopic = "telemetry.sensors.v1";
            kafkaConsumer.subscribe(Collections.singletonList(sensorEventsTopic));
            log.info("Агрегатор запущен. Подписан на топик: {}", sensorEventsTopic);

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = kafkaConsumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    continue;
                }

                log.info("Получено {} записей", records.count());

                records.forEach(record -> {
                    SensorEventAvro event = record.value();
                    aggregatorService.processSensorEvent(event);
                });

                kafkaConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("WakeupException, завершение работы...");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            log.info("Сброс продюсера...");
            if (kafkaProducer != null) {
                kafkaProducer.flush();
            }

            log.info("Фиксация оффсетов...");
            if (kafkaConsumer != null) {
                kafkaConsumer.commitSync();
            }

        } catch (Exception e) {
            log.error("Ошибка во время cleanup", e);
        } finally {
            log.info("Закрываем консьюмер");
            if (kafkaConsumer != null) {
                kafkaConsumer.close();
            }
            log.info("Закрываем продюсер");
            if (kafkaProducer != null) {
                kafkaProducer.close();
            }
            log.info("Агрегатор остановлен");
        }
    }
}