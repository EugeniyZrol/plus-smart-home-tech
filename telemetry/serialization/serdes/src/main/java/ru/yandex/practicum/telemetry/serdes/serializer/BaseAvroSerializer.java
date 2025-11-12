package ru.yandex.practicum.telemetry.serdes.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class BaseAvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {

    private static final Logger log = LoggerFactory.getLogger(BaseAvroSerializer.class);

    protected final Class<T> targetType;

    public BaseAvroSerializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<T> datumWriter = new SpecificDatumWriter<>(targetType);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            datumWriter.write(data, encoder);
            encoder.flush();

            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Ошибка сериализации {}", targetType.getSimpleName(), e);
            throw new SerializationException("Ошибка сериализации " + targetType.getSimpleName(), e);
        }
    }
}