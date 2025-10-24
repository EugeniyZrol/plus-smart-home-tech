package ru.yandex.practicum.telemetry.collector.serdes;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HubEventAvroSerializer implements Serializer<HubEventAvro> {
    private static final Logger log = LoggerFactory.getLogger(HubEventAvroSerializer.class);

    @Override
    public byte[] serialize(String topic, HubEventAvro event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<HubEventAvro> datumWriter = new SpecificDatumWriter<>(HubEventAvro.class);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            datumWriter.write(event, encoder);
            encoder.flush();

            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Ошибка сериализации HubEventAvro", e);
            throw new SerializationException("Ошибка сериализации HubEventAvro", e);
        }
    }
}