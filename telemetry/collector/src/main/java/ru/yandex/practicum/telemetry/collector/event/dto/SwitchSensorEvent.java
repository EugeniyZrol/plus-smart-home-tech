package ru.yandex.practicum.telemetry.collector.event.dto;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.telemetry.collector.event.enums.SensorEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwitchSensorEvent extends SensorEvent {
    @NotNull(message = "Состояние переключателя не может быть null")
    private Boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}