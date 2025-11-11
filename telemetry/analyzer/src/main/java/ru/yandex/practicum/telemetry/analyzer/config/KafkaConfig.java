package ru.yandex.practicum.telemetry.analyzer.config;

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

import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "analyzer-snapshots-group");
        props.put("auto.offset.reset", "latest");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "ru.yandex.practicum.telemetry.analyzer.serdes.SensorsSnapshotDeserializer");
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubEventConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "analyzer-hubevents-group");
        props.put("auto.offset.reset", "latest");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "ru.yandex.practicum.telemetry.analyzer.serdes.HubEventDeserializer");
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, SensorEventAvro> sensorEventConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "analyzer-sensorevents-group");
        props.put("auto.offset.reset", "latest");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "ru.yandex.practicum.telemetry.analyzer.serdes.SensorEventDeserializer");
        return new KafkaConsumer<>(props);
    }

    @Bean
    public Producer<String, DeviceActionAvro> commandProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "ru.yandex.practicum.telemetry.analyzer.serdes.DeviceActionSerializer");
        props.put("acks", "all");
        props.put("retries", 3);
        return new KafkaProducer<>(props);
    }
}