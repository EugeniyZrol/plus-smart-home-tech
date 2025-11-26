package ru.yandex.practicum.telemetry.collector.handler.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.SensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final SensorEventProducer sensorEventProducer;

    @Override
    public boolean canHandle(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEvent event) {
        ClimateSensorEvent climateEvent = (ClimateSensorEvent) event;
        log.info("Обработка климатического события: {}°C, {}% влажности, {} CO2 для датчика {}",
                climateEvent.getTemperatureC(), climateEvent.getHumidity(),
                climateEvent.getCo2Level(), climateEvent.getId());

        sensorEventProducer.sendSensorEvent(climateEvent);
    }
}