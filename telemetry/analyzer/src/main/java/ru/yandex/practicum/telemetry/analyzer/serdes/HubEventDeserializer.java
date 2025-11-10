package ru.yandex.practicum.telemetry.analyzer.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.io.IOException;
import java.util.Map;

public class HubEventDeserializer implements Deserializer<HubEventAvro> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public HubEventAvro deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            DatumReader<HubEventAvro> reader = new SpecificDatumReader<>(HubEventAvro.class);
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка десериализации HubEventAvro", e);
        }
    }

    @Override
    public void close() {
    }
}