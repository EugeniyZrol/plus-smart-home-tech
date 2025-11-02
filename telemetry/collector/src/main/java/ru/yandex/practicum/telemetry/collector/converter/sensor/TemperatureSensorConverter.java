package ru.yandex.practicum.telemetry.collector.converter.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.TemperatureSensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TemperatureSensorConverter implements SensorEventConverter {

    @Override
    public boolean canConvert(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public SensorEvent convert(SensorEventProto proto) {
        TemperatureSensorEvent event = new TemperatureSensorEvent();
        setBaseFields(event, proto);

        var tempSensor = proto.getTemperatureSensor();
        event.setTemperatureC(tempSensor.getTemperatureC());
        event.setTemperatureF(tempSensor.getTemperatureF());

        return event;
    }

    private void setBaseFields(SensorEvent event, SensorEventProto proto) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }
}