package ru.yandex.practicum.telemetry.collector.converter.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ClimateSensorConverter implements SensorEventConverter {

    @Override
    public boolean canConvert(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public SensorEvent convert(SensorEventProto proto) {
        ClimateSensorEvent event = new ClimateSensorEvent();
        setBaseFields(event, proto);

        var climateSensor = proto.getClimateSensor();
        event.setTemperatureC(climateSensor.getTemperatureC());
        event.setHumidity(climateSensor.getHumidity());
        event.setCo2Level(climateSensor.getCo2Level());

        return event;
    }

    private void setBaseFields(SensorEvent event, SensorEventProto proto) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }
}