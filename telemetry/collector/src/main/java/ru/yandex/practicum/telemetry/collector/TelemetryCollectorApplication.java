package ru.yandex.practicum.telemetry.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TelemetryCollectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemetryCollectorApplication.class, args);
    }
}