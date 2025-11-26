package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.scenarios")
public class ScenarioProperties {

    private String turnOffAllLightsName = "Выключить весь свет";
    private List<String> specialScenarios = List.of("Выключить весь свет");
    private int commandDelayMs = 10;
    private int maxRetryAttempts = 3;
}