package ru.yandex.practicum.telemetry.collector.converter.hub;

import ru.yandex.practicum.telemetry.collector.event.dto.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DeviceAddedConverter implements HubEventConverter {

    @Override
    public boolean canConvert(HubEventProto.PayloadCase payloadCase) {
        return payloadCase == HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public HubEvent convert(HubEventProto proto) {
        DeviceAddedEvent event = new DeviceAddedEvent();
        setBaseFields(event, proto);

        var deviceAdded = proto.getDeviceAdded();
        event.setId(deviceAdded.getId());
        event.setDeviceType(ru.yandex.practicum.telemetry.collector.event.enums.DeviceType.valueOf(
                deviceAdded.getType().name()));

        return event;
    }

    private void setBaseFields(HubEvent event, HubEventProto proto) {
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }
}