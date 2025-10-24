package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.HubEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioRemovedEvent extends HubEvent {
    @NotBlank(message = "Название сценария не может быть пустым")
    @Size(min = 3, message = "Название сценария должно содержать минимум 3 символа")
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}