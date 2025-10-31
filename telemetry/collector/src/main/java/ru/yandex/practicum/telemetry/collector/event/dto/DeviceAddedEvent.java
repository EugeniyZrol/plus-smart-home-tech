package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.DeviceType;
import ru.yandex.practicum.telemetry.collector.event.enums.HubEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAddedEvent extends HubEvent {
    @NotBlank(message = "ID устройства не может быть пустым")
    private String id;

    @NotNull(message = "Тип устройства не может быть null")
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}