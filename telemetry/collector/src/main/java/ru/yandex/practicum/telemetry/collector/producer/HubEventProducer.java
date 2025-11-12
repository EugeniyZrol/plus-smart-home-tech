package ru.yandex.practicum.telemetry.collector.producer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.converter.HubEventToAvroConverter;
import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.serdes.serializer.HubEventAvroSerializer;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventProducer {

    @Value("${kafka.topic.telemetry.hubs:telemetry.hubs.v1}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final HubEventToAvroConverter converter;
    private final HubEventAvroSerializer serializer = new HubEventAvroSerializer();

    public void sendHubEvent(HubEvent event) {
        try {
            HubEventAvro avroEvent = converter.convert(event);
            byte[] serializedData = serializer.serialize(topic, avroEvent);

            kafkaTemplate.send(topic, event.getHubId(), serializedData)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Хаб событие успешно отправлено: {} в тему: {}", event.getHubId(), topic);
                        } else {
                            log.error("Ошибка отправки хаба события: {} в тему: {}", event.getHubId(), topic, ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Ошибка конвертации/отправки хаба события: {}", event.getHubId(), e);
        }
    }
}