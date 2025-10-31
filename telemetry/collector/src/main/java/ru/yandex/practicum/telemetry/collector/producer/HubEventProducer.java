package ru.yandex.practicum.telemetry.collector.producer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.converter.HubEventToAvroConverter;
import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.serdes.HubEventAvroSerializer;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventProducer {

    private static final String TOPIC = "telemetry.hubs.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final HubEventToAvroConverter converter;
    private final HubEventAvroSerializer serializer = new HubEventAvroSerializer();

    public void sendHubEvent(HubEvent event) {
        try {
            HubEventAvro avroEvent = converter.convert(event);
            byte[] serializedData = serializer.serialize(TOPIC, avroEvent);

            kafkaTemplate.send(TOPIC, event.getHubId(), serializedData)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Хаб событие успешно отправлено: {} в тему: {}", event.getHubId(), TOPIC);
                        } else {
                            log.error("Ошибка отправки хаба события: {} в тему: {}", event.getHubId(), TOPIC, ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Ошибка конвертации/отправки хаба события: {}", event.getHubId(), e);
        }
    }
}