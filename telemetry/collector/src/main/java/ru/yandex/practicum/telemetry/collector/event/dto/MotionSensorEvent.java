package ru.yandex.practicum.telemetry.collector.event.dto;

import ru.yandex.practicum.telemetry.collector.event.enums.SensorEventType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotionSensorEvent extends SensorEvent {
    @NotNull(message = "Качество связи не может быть null")
    private Integer linkQuality;

    @NotNull(message = "Статус движения не может быть null")
    private Boolean motion;

    @NotNull(message = "Напряжение не может быть null")
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}