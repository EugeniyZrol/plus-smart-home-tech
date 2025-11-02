package ru.yandex.practicum.telemetry.collector.handler.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.SensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final SensorEventProducer sensorEventProducer;

    @Override
    public boolean canHandle(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEvent event) {
        LightSensorEvent lightEvent = (LightSensorEvent) event;
        log.info("Обработка события освещенности: освещенность={}, качество связи={} для датчика {}",
                lightEvent.getLuminosity(), lightEvent.getLinkQuality(), lightEvent.getId());

        sensorEventProducer.sendSensorEvent(lightEvent);
    }
}