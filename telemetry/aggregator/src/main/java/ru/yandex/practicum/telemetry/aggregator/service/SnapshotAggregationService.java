package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SnapshotAggregationService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        log.debug("Обновление состояния для хаба: {}, датчик: {}", hubId, sensorId);

        SensorsSnapshotAvro currentSnapshot = snapshots.get(hubId);
        Map<String, SensorStateAvro> newSensorsState = new HashMap<>();

        if (currentSnapshot != null) {
            newSensorsState.putAll(currentSnapshot.getSensorsState());
            log.debug("Текущий снапшот для хаба {} содержит {} датчиков", hubId, newSensorsState.size());
        } else {
            log.info("Создание нового снапшота для хаба: {}", hubId);
        }

        SensorStateAvro oldState = newSensorsState.get(sensorId);
        boolean needsUpdate = true;

        if (oldState != null) {
            if (oldState.getTimestamp() > event.getTimestamp()) {
                log.debug("Событие устарело для датчика {}: текущий timestamp={}, новый timestamp={}",
                        sensorId, oldState.getTimestamp(), event.getTimestamp());
                needsUpdate = false;
            }
            else if (isDataEqualImproved(oldState.getData(), event.getPayload())) {
                log.debug("Данные не изменились для датчика {}", sensorId);
                needsUpdate = false;
            } else {
                log.debug("Данные изменились для датчика {}", sensorId);
            }
        } else {
            log.debug("Новый датчик {} для хаба {}", sensorId, hubId);
        }

        if (!needsUpdate) {
            log.debug("Обновление не требуется для датчика {}", sensorId);
            return Optional.empty();
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        newSensorsState.put(sensorId, newState);

        long snapshotTimestamp = newSensorsState.values().stream()
                .mapToLong(SensorStateAvro::getTimestamp)
                .max()
                .orElse(event.getTimestamp());

        SensorsSnapshotAvro updatedSnapshot = SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(snapshotTimestamp)
                .setSensorsState(newSensorsState)
                .build();

        snapshots.put(hubId, updatedSnapshot);

        log.info("Снапшот обновлен для hub: {}, sensor: {}, количество датчиков: {}, timestamp: {}",
                hubId, sensorId, newSensorsState.size(), snapshotTimestamp);
        return Optional.of(updatedSnapshot);
    }

    private boolean isDataEqualImproved(Object oldData, Object newData) {
        if (oldData == null && newData == null) return true;
        if (oldData == null || newData == null) return false;

        return oldData.toString().equals(newData.toString());
    }
}