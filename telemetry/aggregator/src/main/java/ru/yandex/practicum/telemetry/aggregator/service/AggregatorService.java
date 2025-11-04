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
        log.info("Обработка события датчика: hub={}, sensor={}, timestamp={}",
                event.getHubId(), event.getId(), event.getTimestamp());

        try {
            Optional<SensorsSnapshotAvro> updatedSnapshot = aggregationService.updateState(event);

            if (updatedSnapshot.isPresent()) {
                log.info("Отправка обновленного снапшота для hub: {}", event.getHubId());
                producerService.sendSnapshot(updatedSnapshot.get());
            } else {
                log.info("Обновление не требуется для hub: {}, sensor: {}", event.getHubId(), event.getId());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика для hub: {}, sensor: {}",
                    event.getHubId(), event.getId(), e);
        }
    }
}