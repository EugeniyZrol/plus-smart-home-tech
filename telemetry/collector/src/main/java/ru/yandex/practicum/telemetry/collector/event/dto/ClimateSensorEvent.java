package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.SensorEventType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClimateSensorEvent extends SensorEvent {
    @NotNull(message = "Температура не может быть null")
    private Integer temperatureC;

    @NotNull(message = "Влажность не может быть null")
    private Integer humidity;

    @NotNull(message = "Уровень CO2 не может быть null")
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
