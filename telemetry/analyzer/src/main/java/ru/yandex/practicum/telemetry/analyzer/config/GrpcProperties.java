package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.grpc")
public class GrpcProperties {

    private List<String> retryStatusCodes = List.of("UNAVAILABLE", "DEADLINE_EXCEEDED");
    private int retryDelayMs = 100;
}