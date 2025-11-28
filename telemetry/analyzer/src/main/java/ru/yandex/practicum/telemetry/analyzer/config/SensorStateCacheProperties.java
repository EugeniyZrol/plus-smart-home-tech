package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sensor-state.cache")
public class SensorStateCacheProperties {
    private boolean enabled;
    private long ttlMinutes;
    private int maxSize;
    private long cleanupIntervalMinutes;
}