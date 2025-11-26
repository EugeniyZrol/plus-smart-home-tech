package ru.yandex.practicum.telemetry.serdes.serializer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventAvroSerializer extends BaseAvroSerializer<HubEventAvro> {
    public HubEventAvroSerializer() {
        super(HubEventAvro.class);
    }
}