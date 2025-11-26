package ru.yandex.practicum.telemetry.collector.producer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.converter.SensorEventToAvroConverter;
import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.serdes.serializer.SensorEventAvroSerializer;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventProducer {

    @Value("${kafka.topic.telemetry.sensors:telemetry.sensors.v1}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SensorEventToAvroConverter converter;
    private final SensorEventAvroSerializer serializer = new SensorEventAvroSerializer();

    public void sendSensorEvent(SensorEvent event) {
        try {
            SensorEventAvro avroEvent = converter.convert(event);
            byte[] serializedData = serializer.serialize(topic, avroEvent);

            kafkaTemplate.send(topic, event.getId(), serializedData)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Сенсорное событие успешно отправлено: {} в тему: {}", event.getId(), topic);
                        } else {
                            log.error("Ошибка отправки сенсорного события: {} в тему: {}", event.getId(), topic, ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Ошибка конвертации/отправки сенсорного события: {}", event.getId(), e);
        }
    }
}