package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.HubEventType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank(message = "ID устройства не может быть пустым")
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}