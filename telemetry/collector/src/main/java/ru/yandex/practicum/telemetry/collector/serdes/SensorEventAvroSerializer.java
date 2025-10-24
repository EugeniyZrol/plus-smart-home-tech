package ru.yandex.practicum.telemetry.collector.serdes;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SensorEventAvroSerializer implements Serializer<SensorEventAvro> {
    private static final Logger log = LoggerFactory.getLogger(SensorEventAvroSerializer.class);

    @Override
    public byte[] serialize(String topic, SensorEventAvro event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<SensorEventAvro> datumWriter = new SpecificDatumWriter<>(SensorEventAvro.class);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            datumWriter.write(event, encoder);
            encoder.flush();

            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Ошибка сериализации SensorEventAvro", e);
            throw new SerializationException("Ошибка сериализации SensorEventAvro", e);
        }
    }
}