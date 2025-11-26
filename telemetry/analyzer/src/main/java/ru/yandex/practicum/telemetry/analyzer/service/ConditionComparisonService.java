package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.config.ComparisonProperties;
import ru.yandex.practicum.telemetry.analyzer.entity.Condition;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConditionComparisonService {

    private final ComparisonProperties comparisonProperties;

    public boolean isConditionMet(Condition condition, Object sensorData) {
        int conditionValue = condition.getValue();

        return switch (condition.getType()) {
            case TEMPERATURE -> {
                Integer temperature = extractTemperature(sensorData);
                yield temperature != null && compareValues(temperature, conditionValue, condition.getOperation());
            }
            case MOTION -> sensorData instanceof MotionSensorAvro &&
                    ((MotionSensorAvro) sensorData).getMotion() == (conditionValue == 1);
            case LUMINOSITY -> sensorData instanceof LightSensorAvro &&
                    compareValues(((LightSensorAvro) sensorData).getLuminosity(), conditionValue, condition.getOperation());
            case SWITCH -> sensorData instanceof SwitchSensorAvro &&
                    ((SwitchSensorAvro) sensorData).getState() == (conditionValue == 1);
            case CO2LEVEL -> {
                Integer co2Level = extractCo2Level(sensorData);
                yield co2Level != null && compareValues(co2Level, conditionValue, condition.getOperation());
            }
            case HUMIDITY -> {
                Integer humidity = extractHumidity(sensorData);
                yield humidity != null && compareValues(humidity, conditionValue, condition.getOperation());
            }
            default -> {
                log.warn("Unknown condition type: {}", condition.getType());
                yield false;
            }
        };
    }

    private Integer extractTemperature(Object sensorData) {
        if (sensorData instanceof TemperatureSensorAvro) {
            return ((TemperatureSensorAvro) sensorData).getTemperatureC();
        } else if (sensorData instanceof ClimateSensorAvro) {
            return ((ClimateSensorAvro) sensorData).getTemperatureC();
        }
        return null;
    }

    private Integer extractCo2Level(Object sensorData) {
        return sensorData instanceof ClimateSensorAvro ?
                ((ClimateSensorAvro) sensorData).getCo2Level() : null;
    }

    private Integer extractHumidity(Object sensorData) {
        return sensorData instanceof ClimateSensorAvro ?
                ((ClimateSensorAvro) sensorData).getHumidity() : null;
    }

    private boolean compareValues(int actual, int expected, ConditionOperationAvro operation) {
        if (!comparisonProperties.getAllowedOperations().contains(operation.name())) {
            log.warn("Operation not allowed: {}", operation);
            return false;
        }

        return switch (operation) {
            case EQUALS -> actual == expected;
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
        };
    }
}