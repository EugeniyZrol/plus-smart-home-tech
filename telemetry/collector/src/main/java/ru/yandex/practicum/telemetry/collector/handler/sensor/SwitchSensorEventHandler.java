package ru.yandex.practicum.telemetry.collector.handler.sensor;

import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.SensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final SensorEventProducer sensorEventProducer;

    @Override
    public boolean canHandle(SensorEventProto.PayloadCase payloadCase) {
        return payloadCase == SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEvent event) {
        SwitchSensorEvent switchEvent = (SwitchSensorEvent) event;
        log.info("Обработка события переключателя: состояние={} для датчика {}",
                switchEvent.getState(), switchEvent.getId());

        sensorEventProducer.sendSensorEvent(switchEvent);
    }
}