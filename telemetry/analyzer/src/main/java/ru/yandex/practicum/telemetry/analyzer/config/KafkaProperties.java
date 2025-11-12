package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {

    private String snapshotsGroupId = "analyzer-snapshots-group";
    private String hubEventsGroupId = "analyzer-hubevents-group";
    private String sensorEventsGroupId = "analyzer-sensorevents-group";

    private String hubsTopic = "telemetry.hubs.v1";
    private String sensorsTopic = "telemetry.sensors.v1";
    private String snapshotsTopic = "telemetry.snapshots.v1";

    private String autoOffsetReset = "latest";
    private boolean enableAutoCommit = false;
    private int pollTimeoutMs = 100;
    private int sessionTimeoutMs = 30000;
    private int maxPollIntervalMs = 300000;
    private int maxPollRecords = 500;

    private String acks = "all";
    private int retries = 3;
    private int lingerMs = 0;
    private int batchSize = 16384;
    private int bufferMemory = 33554432;
    private int requestTimeoutMs = 30000;
    private int deliveryTimeoutMs = 120000;
    private int heartbeatIntervalMs = 3000;
}