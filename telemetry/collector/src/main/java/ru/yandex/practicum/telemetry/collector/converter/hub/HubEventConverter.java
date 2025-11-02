package ru.yandex.practicum.telemetry.collector.converter.hub;

import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventConverter {

    boolean canConvert(HubEventProto.PayloadCase payloadCase);

    HubEvent convert(HubEventProto proto);

    default HubEventProto.PayloadCase getSupportedType() {
        throw new UnsupportedOperationException("Метод должен быть реализован");
    }
}