package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorStateService {

    private final Map<String, Map<String, Object>> hubSensorsState = new ConcurrentHashMap<>();

    public void updateSensorState(String hubId, String sensorId, Object sensorData) {
        Map<String, Object> hubSensors = hubSensorsState.computeIfAbsent(hubId, k -> new ConcurrentHashMap<>());

        Object oldData = hubSensors.get(sensorId);
        hubSensors.put(sensorId, sensorData);

        log.debug("Обновлено состояние датчика {} для хаба {}. Старые данные: {}, Новые данные: {}",
                sensorId, hubId, oldData != null ? oldData.getClass().getSimpleName() : "null",
                sensorData.getClass().getSimpleName());
    }

    public Object getSensorState(String hubId, String sensorId) {
        Map<String, Object> hubSensors = hubSensorsState.get(hubId);
        if (hubSensors != null) {
            Object data = hubSensors.get(sensorId);
            log.debug("Получено состояние датчика {} для хаба {}: {}",
                    sensorId, hubId, data != null ? data.getClass().getSimpleName() : "null");
            return data;
        }
        log.debug("Хаб {} не найден в состоянии датчиков", hubId);
        return null;
    }
}