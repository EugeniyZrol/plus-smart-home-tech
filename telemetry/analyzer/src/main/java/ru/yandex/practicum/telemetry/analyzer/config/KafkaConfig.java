package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final KafkaConfigProperties aggregatorKafkaProperties;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        Map<String, Object> props = buildConsumerProperties(aggregatorKafkaProperties.getConsumer().getSnapshotsGroupId());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubEventConsumer() {
        Map<String, Object> props = buildConsumerProperties(aggregatorKafkaProperties.getConsumer().getHubEventsGroupId());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, SensorEventAvro> sensorEventConsumer() {
        Map<String, Object> props = buildConsumerProperties(aggregatorKafkaProperties.getConsumer().getSensorEventsGroupId());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Bean
    public Producer<String, DeviceActionAvro> commandProducer() {
        Map<String, Object> props = buildProducerProperties();
        props.put("value.serializer", DeviceActionSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    private Map<String, Object> buildConsumerProperties(String groupId) {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getConsumer().getEnableAutoCommit());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getKeyDeserializer());

        if (aggregatorKafkaProperties.getConsumer().getProperties() != null) {
            props.putAll(aggregatorKafkaProperties.getConsumer().getProperties());
        }

        if (kafkaProperties.getConsumer().getProperties() != null) {
            props.putAll(kafkaProperties.getConsumer().getProperties());
        }

        return props;
    }

    private Map<String, Object> buildProducerProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getKeySerializer());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducer().getRetries());

        if (kafkaProperties.getProducer().getBatchSize() != null) {
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, (int) kafkaProperties.getProducer().getBatchSize().toBytes());
        }
        if (kafkaProperties.getProducer().getBufferMemory() != null) {
            props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaProperties.getProducer().getBufferMemory().toBytes());
        }

        if (kafkaProperties.getProducer().getProperties() != null) {
            props.putAll(kafkaProperties.getProducer().getProperties());
        }

        if (aggregatorKafkaProperties.getProducer().getProperties() != null) {
            props.putAll(aggregatorKafkaProperties.getProducer().getProperties());
        }

        return props;
    }
}