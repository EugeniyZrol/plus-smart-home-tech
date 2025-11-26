package ru.yandex.practicum.telemetry.collector.handler.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {

    boolean canHandle(SensorEventProto.PayloadCase payloadCase);

    void handle(SensorEvent event);

    default SensorEventProto.PayloadCase getSupportedType() {
        throw new UnsupportedOperationException("Метод должен быть реализован");
    }
}