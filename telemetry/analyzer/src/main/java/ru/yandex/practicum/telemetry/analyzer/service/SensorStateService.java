package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.config.SensorStateCacheProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SensorStateService {

    private final SensorStateCacheProperties cacheProperties;
    private final Map<String, Map<String, Object>> hubSensorsState = new ConcurrentHashMap<>();

    public void updateSensorState(String hubId, String sensorId, Object sensorData) {
        if (!cacheProperties.isEnabled()) {
            return;
        }

        Map<String, Object> hubSensors = hubSensorsState.computeIfAbsent(hubId, k -> new ConcurrentHashMap<>());

        if (hubSensors.size() >= cacheProperties.getMaxSize()) {
            return;
        }

        hubSensors.put(sensorId, sensorData);
    }

    public Object getSensorState(String hubId, String sensorId) {
        if (!cacheProperties.isEnabled()) {
            return null;
        }

        Map<String, Object> hubSensors = hubSensorsState.get(hubId);
        return hubSensors != null ? hubSensors.get(sensorId) : null;
    }
}