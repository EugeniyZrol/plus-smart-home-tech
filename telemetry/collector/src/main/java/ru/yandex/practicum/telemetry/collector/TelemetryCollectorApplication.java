package ru.yandex.practicum.telemetry.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelemetryCollectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemetryCollectorApplication.class, args);
    }
}