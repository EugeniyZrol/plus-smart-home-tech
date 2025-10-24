package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.SensorEventType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemperatureSensorEvent extends SensorEvent {
    @NotNull(message = "Температура в Цельсиях не может быть null")
    private Integer temperatureC;

    @NotNull(message = "Температура в Фаренгейтах не может быть null")
    private Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}