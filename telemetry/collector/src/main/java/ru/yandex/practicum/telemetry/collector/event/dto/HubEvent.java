package ru.yandex.practicum.telemetry.collector.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.yandex.practicum.telemetry.collector.event.enums.HubEventType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@Getter
@Setter
public abstract class HubEvent {
    @NotBlank(message = "ID хаба не может быть пустым")
    private String hubId;

    private Instant timestamp = Instant.now();

    public abstract HubEventType getType();
}