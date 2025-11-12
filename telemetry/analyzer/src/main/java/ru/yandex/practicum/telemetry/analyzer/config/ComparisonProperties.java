package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.conditions")
public class ComparisonProperties {

    private List<String> allowedOperations = List.of("EQUALS", "GREATER_THAN", "LOWER_THAN");
}