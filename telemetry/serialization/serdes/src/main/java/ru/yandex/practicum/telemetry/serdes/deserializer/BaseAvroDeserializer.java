package ru.yandex.practicum.telemetry.serdes.deserializer;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public abstract class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private static final Logger log = LoggerFactory.getLogger(BaseAvroDeserializer.class);

    protected final Class<T> targetType;
    protected final DatumReader<T> datumReader;

    public BaseAvroDeserializer(Class<T> targetType) {
        this.targetType = targetType;
        this.datumReader = new SpecificDatumReader<>(targetType);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Конфигурация не требуется
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            Decoder decoder = DecoderFactory.get().binaryDecoder(inputStream, null);
            return datumReader.read(null, decoder);
        } catch (IOException e) {
            log.error("Ошибка десериализации {} для топика: {}", targetType.getSimpleName(), topic, e);
            throw new RuntimeException("Ошибка десериализации " + targetType.getSimpleName(), e);
        }
    }

    @Override
    public void close() {
        // Ресурсы не требуют закрытия
    }
}