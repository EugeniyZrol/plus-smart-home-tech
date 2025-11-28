package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfigProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotProducerService {

    private final Producer<String, SensorsSnapshotAvro> kafkaProducer;
    private final KafkaConfigProperties kafkaConfigProperties;

    public void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            kafkaProducer.send(new ProducerRecord<>(
                            kafkaConfigProperties.getProducer().getTopic(),
                            null,
                            System.currentTimeMillis(),
                            snapshot.getHubId(),
                            snapshot),
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