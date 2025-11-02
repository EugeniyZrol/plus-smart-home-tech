package ru.yandex.practicum.telemetry.collector.converter.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LightSensorConverter implements SensorEventConverter {

    @Override
    public boolean canConvert(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public SensorEvent convert(SensorEventProto proto) {
        LightSensorEvent event = new LightSensorEvent();
        setBaseFields(event, proto);

        var lightSensor = proto.getLightSensor();
        event.setLinkQuality(lightSensor.getLinkQuality());
        event.setLuminosity(lightSensor.getLuminosity());

        return event;
    }

    private void setBaseFields(SensorEvent event, SensorEventProto proto) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }
}