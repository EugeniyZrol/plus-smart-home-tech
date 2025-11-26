package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotProducerService {

    private final Producer<String, SensorsSnapshotAvro> kafkaProducer;

    @Value("${kafka.topics.snapshots:telemetry.snapshots.v1}")
    private String snapshotTopic;

    public void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            kafkaProducer.send(new ProducerRecord<>(
                            snapshotTopic, null, System.currentTimeMillis(),
                            snapshot.getHubId(), snapshot),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Ошибка отправки снапшота для hub: {}", snapshot.getHubId(), exception);
                        }
                    });

        } catch (Exception e) {
            log.error("Ошибка отправки снапшота для hub: {}", snapshot.getHubId(), e);
        }
    }
}