package ru.yandex.practicum.telemetry.collector.handler.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.TemperatureSensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.SensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final SensorEventProducer sensorEventProducer;

    @Override
    public boolean canHandle(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(SensorEvent event) {
        TemperatureSensorEvent tempEvent = (TemperatureSensorEvent) event;
        log.info("Обработка температурного события: {}°C, {}°F для датчика {}",
                tempEvent.getTemperatureC(), tempEvent.getTemperatureF(), tempEvent.getId());

        sensorEventProducer.sendSensorEvent(tempEvent);
    }
}