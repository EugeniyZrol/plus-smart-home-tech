package ru.yandex.practicum.telemetry.analyzer.serdes;

import ru.yandex.practicum.telemetry.aggregator.serdes.deserializer.BaseAvroDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventDeserializer extends BaseAvroDeserializer<HubEventAvro> {
    public HubEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}