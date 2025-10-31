package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.ConditionType;
import ru.yandex.practicum.telemetry.collector.event.enums.ConditionOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private Integer value;
}