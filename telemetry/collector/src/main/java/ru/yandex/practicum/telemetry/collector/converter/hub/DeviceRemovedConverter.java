package ru.yandex.practicum.telemetry.collector.converter.hub;

import ru.yandex.practicum.telemetry.collector.event.dto.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DeviceRemovedConverter implements HubEventConverter {

    @Override
    public boolean canConvert(HubEventProto.PayloadCase payloadCase) {
        return payloadCase == HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public HubEvent convert(HubEventProto proto) {
        DeviceRemovedEvent event = new DeviceRemovedEvent();
        setBaseFields(event, proto);

        var deviceRemoved = proto.getDeviceRemoved();
        event.setId(deviceRemoved.getId());

        return event;
    }

    private void setBaseFields(HubEvent event, HubEventProto proto) {
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }
}