package ru.yandex.practicum.telemetry.collector.converter.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MotionSensorConverter implements SensorEventConverter {

    @Override
    public boolean canConvert(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public SensorEvent convert(SensorEventProto proto) {
        MotionSensorEvent event = new MotionSensorEvent();
        setBaseFields(event, proto);

        var motionSensor = proto.getMotionSensor();
        event.setLinkQuality(motionSensor.getLinkQuality());
        event.setMotion(motionSensor.getMotion());
        event.setVoltage(motionSensor.getVoltage());

        return event;
    }

    private void setBaseFields(SensorEvent event, SensorEventProto proto) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }
}