package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorService {

    private final SnapshotAggregationService aggregationService;
    private final SnapshotProducerService producerService;

    public void processSensorEvent(SensorEventAvro event) {
        try {
            Optional<SensorsSnapshotAvro> updatedSnapshot = aggregationService.updateState(event);

            if (updatedSnapshot.isPresent()) {
                producerService.sendSnapshot(updatedSnapshot.get());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика для hub: {}, sensor: {}",
                    event.getHubId(), event.getId(), e);
        }
    }
}