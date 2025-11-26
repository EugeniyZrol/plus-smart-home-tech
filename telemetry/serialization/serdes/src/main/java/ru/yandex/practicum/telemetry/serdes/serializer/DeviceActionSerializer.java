package ru.yandex.practicum.telemetry.serdes.serializer;

import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

public class DeviceActionSerializer extends BaseAvroSerializer<DeviceActionAvro> {
    public DeviceActionSerializer() {
        super(DeviceActionAvro.class);
    }
}