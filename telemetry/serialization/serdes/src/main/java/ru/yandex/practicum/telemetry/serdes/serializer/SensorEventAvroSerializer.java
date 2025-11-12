package ru.yandex.practicum.telemetry.serdes.serializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventAvroSerializer extends BaseAvroSerializer<SensorEventAvro> {
    public SensorEventAvroSerializer() {
        super(SensorEventAvro.class);
    }
}