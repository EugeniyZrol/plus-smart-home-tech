package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sensor-state.cache")
public class SensorStateCacheProperties {

    private boolean enabled = true;
    private long ttlMinutes = 60;
    private int maxSize = 10000;
    private long cleanupIntervalMinutes = 5;
}