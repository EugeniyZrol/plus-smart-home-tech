package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotProducerService {

    private final Producer<String, SensorsSnapshotAvro> kafkaProducer;

    public void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            String snapshotTopic = "telemetry.snapshots.v1";

            kafkaProducer.send(new ProducerRecord<>(
                            snapshotTopic, null, System.currentTimeMillis(),
                            snapshot.getHubId(), snapshot),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Ошибка отправки снапшота для hub: {}", snapshot.getHubId(), exception);
                        } else {
                            log.info("Снапшот отправлен для hub: {}, offset: {}",
                                    snapshot.getHubId(), metadata.offset());
                        }
                    });

            kafkaProducer.flush();

        } catch (Exception e) {
            log.error("Ошибка отправки снапшота для hub: {}", snapshot.getHubId(), e);
        }
    }
}