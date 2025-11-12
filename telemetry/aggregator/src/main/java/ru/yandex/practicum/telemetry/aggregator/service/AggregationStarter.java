package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaConsumer<String, SensorEventAvro> kafkaConsumer;
    private final Producer<String, SensorsSnapshotAvro> kafkaProducer;
    private final AggregatorService aggregatorService;

    @Value("${kafka.topics.sensor-events:telemetry.sensors.v1}")
    private String sensorEventsTopic;

    private final ConcurrentHashMap<TopicPartition, OffsetAndMetadata> pendingOffsets = new ConcurrentHashMap<>();

    public void start() {
        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Запущен graceful shutdown...");
            kafkaConsumer.wakeup();
        }));

        try {
            kafkaConsumer.subscribe(Collections.singletonList(sensorEventsTopic));
            log.info("Агрегатор запущен. Подписан на топик: {}", sensorEventsTopic);

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = kafkaConsumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    continue;
                }

                processRecordsWithManualOffsetManagement(records);
            }

        } catch (WakeupException ignored) {
            log.info("WakeupException, завершение работы...");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            cleanup();
        }
    }

    private void processRecordsWithManualOffsetManagement(ConsumerRecords<String, SensorEventAvro> records) {
        Map<TopicPartition, OffsetAndMetadata> currentBatchOffsets = new HashMap<>();

        try {
            records.forEach(record -> {
                try {
                    SensorEventAvro event = record.value();

                    if (event == null) {
                        TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                        currentBatchOffsets.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));
                        pendingOffsets.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));
                        return;
                    }

                    aggregatorService.processSensorEvent(event);

                    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                    currentBatchOffsets.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));
                    pendingOffsets.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));

                } catch (Exception e) {
                    throw new RuntimeException("Ошибка обработки сообщения", e);
                }
            });

            if (!currentBatchOffsets.isEmpty()) {
                kafkaConsumer.commitAsync(currentBatchOffsets, (offsets, exception) -> {
                    if (exception != null) {
                        // ignore
                    } else {
                        offsets.forEach((tp, offset) -> {
                            if (pendingOffsets.get(tp) != null &&
                                    pendingOffsets.get(tp).offset() <= offset.offset()) {
                                pendingOffsets.remove(tp);
                            }
                        });
                    }
                });
            }

        } catch (Exception e) {
            commitPendingOffsetsSync();
        }
    }

    private void commitPendingOffsetsSync() {
        if (!pendingOffsets.isEmpty()) {
            try {
                kafkaConsumer.commitSync(new HashMap<>(pendingOffsets));
                pendingOffsets.clear();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void cleanup() {
        try {
            commitPendingOffsetsSync();

            if (kafkaProducer != null) {
                kafkaProducer.flush();
            }

        } catch (Exception e) {
            // ignore
        } finally {
            if (kafkaConsumer != null) {
                kafkaConsumer.close();
            }
            if (kafkaProducer != null) {
                kafkaProducer.close();
            }
            log.info("Агрегатор остановлен");
        }
    }
}