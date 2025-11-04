package ru.yandex.practicum.telemetry.collector.serdes.serializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class SensorEventAvroSerializer implements Serializer<SensorEventAvro> {

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