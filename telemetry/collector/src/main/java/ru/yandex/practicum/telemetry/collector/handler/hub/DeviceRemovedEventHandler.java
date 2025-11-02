package ru.yandex.practicum.telemetry.collector.handler.hub;

import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.producer.HubEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final HubEventProducer hubEventProducer;

    @Override
    public boolean canHandle(HubEventProto.PayloadCase payloadCase) {
        return payloadCase == HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEvent event) {
        DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
        log.info("Обработка события удаления устройства: {} из хаба {}",
                deviceRemovedEvent.getId(), deviceRemovedEvent.getHubId());

        hubEventProducer.sendHubEvent(deviceRemovedEvent);
    }
}