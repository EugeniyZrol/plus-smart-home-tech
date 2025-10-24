package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.ActionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}