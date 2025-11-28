package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.scenarios")
public class ScenarioProperties {
    private String turnOffAllLightsName;
    private List<String> specialScenarios;
    private int commandDelayMs;
    private int maxRetryAttempts;
}