package ru.yandex.practicum.telemetry.collector.converter;

import ru.yandex.practicum.telemetry.collector.converter.hub.HubEventConverter;
import ru.yandex.practicum.telemetry.collector.converter.sensor.SensorEventConverter;
import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProtobufToDtoConverter {

    private final Map<SensorEventProto.PayloadCase, SensorEventConverter> sensorConverters;
    private final Map<HubEventProto.PayloadCase, HubEventConverter> hubConverters;

    public ProtobufToDtoConverter(List<SensorEventConverter> sensorConverterList,
                                  List<HubEventConverter> hubConverterList) {
        this.sensorConverters = sensorConverterList.stream()
                .collect(Collectors.toMap(
                        SensorEventConverter::getSupportedType,
                        Function.identity()
                ));

        this.hubConverters = hubConverterList.stream()
                .collect(Collectors.toMap(
                        HubEventConverter::getSupportedType,
                        Function.identity()
                ));
    }

    public SensorEvent convertToDto(SensorEventProto proto) {
        SensorEventProto.PayloadCase payloadCase = proto.getPayloadCase();

        SensorEventConverter converter = sensorConverters.get(payloadCase);
        if (converter == null) {
            throw new IllegalArgumentException("Не найден конвертер для типа сенсора: " + payloadCase);
        }

        return converter.convert(proto);
    }

    public HubEvent convertToDto(HubEventProto proto) {
        HubEventProto.PayloadCase payloadCase = proto.getPayloadCase();

        HubEventConverter converter = hubConverters.get(payloadCase);
        if (converter == null) {
            throw new IllegalArgumentException("Не найден конвертер для типа хаба: " + payloadCase);
        }

        return converter.convert(proto);
    }
}