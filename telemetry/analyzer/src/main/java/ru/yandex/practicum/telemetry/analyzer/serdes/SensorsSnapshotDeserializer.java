package ru.yandex.practicum.telemetry.analyzer.serdes;

import ru.yandex.practicum.telemetry.aggregator.serdes.deserializer.BaseAvroDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorsSnapshotDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {
    public SensorsSnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}