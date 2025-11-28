package ru.yandex.practicum.telemetry.aggregator.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfigProperties {
    private ProducerConfig producer;
    private ConsumerConfig consumer;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProducerConfig {
        private String topic;
        private Map<String, Object> properties;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumerConfig {
        private String topic;
        private Duration pollTimeout;
        private Map<String, Object> properties;
    }
}