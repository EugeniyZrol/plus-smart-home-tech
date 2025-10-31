package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.HubEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank(message = "Название сценария не может быть пустым")
    @Size(min = 3, message = "Название сценария должно содержать минимум 3 символа")
    private String name;

    @NotNull(message = "Условия не могут быть null")
    @Size(min = 1, message = "Должно быть хотя бы одно условие")
    private List<ScenarioCondition> conditions;

    @NotNull(message = "Действия не могут быть null")
    @Size(min = 1, message = "Должно быть хотя бы одно действие")
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}