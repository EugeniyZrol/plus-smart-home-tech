package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.telemetry.serdes.deserializer.HubEventDeserializer;
import ru.yandex.practicum.telemetry.serdes.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.telemetry.serdes.deserializer.SensorsSnapshotDeserializer;
import ru.yandex.practicum.telemetry.serdes.serializer.DeviceActionSerializer;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        Properties props = createConsumerProperties();
        props.put("group.id", kafkaProperties.getSnapshotsGroupId());
        props.put("value.deserializer", SensorsSnapshotDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubEventConsumer() {
        Properties props = createConsumerProperties();
        props.put("group.id", kafkaProperties.getHubEventsGroupId());
        props.put("value.deserializer", HubEventDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, SensorEventAvro> sensorEventConsumer() {
        Properties props = createConsumerProperties();
        props.put("group.id", kafkaProperties.getSensorEventsGroupId());
        props.put("value.deserializer", SensorEventDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public Producer<String, DeviceActionAvro> commandProducer() {
        Properties props = createProducerProperties();
        props.put("value.serializer", DeviceActionSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    private Properties createConsumerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("auto.offset.reset", kafkaProperties.getAutoOffsetReset());
        props.put("enable.auto.commit", kafkaProperties.isEnableAutoCommit());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("session.timeout.ms", kafkaProperties.getSessionTimeoutMs());
        props.put("max.poll.interval.ms", kafkaProperties.getMaxPollIntervalMs());
        props.put("max.poll.records", kafkaProperties.getMaxPollRecords());
        props.put("heartbeat.interval.ms", kafkaProperties.getHeartbeatIntervalMs());
        return props;
    }

    private Properties createProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", kafkaProperties.getAcks());
        props.put("retries", kafkaProperties.getRetries());
        props.put("linger.ms", kafkaProperties.getLingerMs());
        props.put("batch.size", kafkaProperties.getBatchSize());
        props.put("buffer.memory", kafkaProperties.getBufferMemory());
        props.put("request.timeout.ms", kafkaProperties.getRequestTimeoutMs());
        props.put("delivery.timeout.ms", kafkaProperties.getDeliveryTimeoutMs());
        return props;
    }
}