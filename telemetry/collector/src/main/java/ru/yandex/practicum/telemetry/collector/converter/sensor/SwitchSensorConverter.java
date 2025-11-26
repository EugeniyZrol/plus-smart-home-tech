package ru.yandex.practicum.telemetry.collector.converter.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SwitchSensorConverter implements SensorEventConverter {

    @Override
    public boolean canConvert(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public SensorEvent convert(SensorEventProto proto) {
        SwitchSensorEvent event = new SwitchSensorEvent();
        setBaseFields(event, proto);

        var switchSensor = proto.getSwitchSensor();
        event.setState(switchSensor.getState());

        return event;
    }

    private void setBaseFields(SensorEvent event, SensorEventProto proto) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }
}