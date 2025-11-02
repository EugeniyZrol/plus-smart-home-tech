package ru.yandex.practicum.telemetry.collector.handler.hub;

import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {

    boolean canHandle(HubEventProto.PayloadCase payloadCase);

    void handle(HubEvent event);

    default HubEventProto.PayloadCase getSupportedType() {
        throw new UnsupportedOperationException("Метод должен быть реализован");
    }
}