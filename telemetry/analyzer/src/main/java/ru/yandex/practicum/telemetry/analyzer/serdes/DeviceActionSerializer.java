package ru.yandex.practicum.telemetry.analyzer.serdes;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DeviceActionSerializer implements Serializer<DeviceActionAvro> {

    @Override
    public byte[] serialize(String topic, DeviceActionAvro data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            DatumWriter<DeviceActionAvro> writer = new SpecificDatumWriter<>(DeviceActionAvro.getClassSchema());
            writer.write(data, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации DeviceActionAvro", e);
        }
    }
}