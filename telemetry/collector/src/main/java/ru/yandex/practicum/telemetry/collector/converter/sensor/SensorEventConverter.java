package ru.yandex.practicum.telemetry.collector.converter.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventConverter {

    boolean canConvert(SensorEventProto.PayloadCase payloadCase);

    SensorEvent convert(SensorEventProto proto);

    default SensorEventProto.PayloadCase getSupportedType() {
        throw new UnsupportedOperationException("Метод должен быть реализован");
    }
}