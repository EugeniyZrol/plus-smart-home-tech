package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfigProperties {
    private ProducerConfig producer;
    private ConsumerConfig consumer;

    @Data
    public static class ProducerConfig {
        private String topic;
        private Map<String, Object> properties;
    }

    @Data
    public static class ConsumerConfig {
        private String snapshotsTopic;
        private String hubEventsTopic;
        private String sensorEventsTopic;
        private String snapshotsGroupId;
        private String hubEventsGroupId;
        private String sensorEventsGroupId;
        private Duration pollTimeout;
        private Map<String, Object> properties;
    }
}