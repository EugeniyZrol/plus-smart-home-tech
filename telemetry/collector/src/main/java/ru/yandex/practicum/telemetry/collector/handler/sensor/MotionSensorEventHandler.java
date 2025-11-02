package ru.yandex.practicum.telemetry.collector.handler.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.SensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final SensorEventProducer sensorEventProducer;

    @Override
    public boolean canHandle(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public void handle(SensorEvent event) {
        MotionSensorEvent motionEvent = (MotionSensorEvent) event;
        log.info("Обработка события движения: движение={}, качество связи={}, напряжение={}V для датчика {}",
                motionEvent.getMotion(), motionEvent.getLinkQuality(),
                motionEvent.getVoltage(), motionEvent.getId());

        sensorEventProducer.sendSensorEvent(motionEvent);
    }
}