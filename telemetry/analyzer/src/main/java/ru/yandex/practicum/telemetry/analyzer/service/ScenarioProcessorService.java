package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.repository.*;
import ru.yandex.practicum.telemetry.collector.event.enums.ActionType;
import ru.yandex.practicum.telemetry.collector.event.enums.ConditionOperation;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioProcessorService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final GrpcCommandService grpcCommandService;
    private final SensorStateService sensorStateService;

    @Transactional
    public void processSensorEvent(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();
        String eventType = getEventType(event);

        log.info("Обработка события от датчика {} для хаба {} (тип: {})", sensorId, hubId, eventType);

        sensorStateService.updateSensorState(hubId, sensorId, event.getPayload());

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        for (Scenario scenario : scenarios) {
            if (isScenarioTriggered(scenario, event, sensorId, eventType)) {
                log.info("Сценарий '{}' активирован событием от датчика {}", scenario.getName(), sensorId);
                executeScenarioActions(scenario);
            }
        }
    }

    private boolean isScenarioTriggered(Scenario scenario, SensorEventAvro event, String triggeringSensorId, String eventType) {
        List<ScenarioCondition> allConditions = scenarioConditionRepository.findByScenarioId(scenario.getId());

        if (allConditions.isEmpty()) {
            return false;
        }

        for (ScenarioCondition scenarioCondition : allConditions) {
            Condition condition = scenarioCondition.getCondition();
            String conditionSensorId = scenarioCondition.getSensor().getId();

            Object sensorData = getSensorDataForCondition(scenario.getHubId(), conditionSensorId, event, triggeringSensorId, eventType);

            if (sensorData == null) {
                log.debug("Данные датчика {} недоступны для проверки условия", conditionSensorId);
                return false;
            }

            if (!checkCondition(condition, sensorData)) {
                log.debug("Условие не выполнено для датчика {}: {} {} {}",
                        conditionSensorId, condition.getType(), condition.getOperation(), condition.getValue());
                return false;
            }
        }

        return true;
    }

    private Object getSensorDataForCondition(String hubId, String conditionSensorId, SensorEventAvro currentEvent, String triggeringSensorId, String eventType) {
        if (conditionSensorId.equals(triggeringSensorId)) {
            return extractSensorData(currentEvent, eventType);
        }

        return sensorStateService.getSensorState(hubId, conditionSensorId);
    }

    private Object extractSensorData(SensorEventAvro event, String eventType) {
        Object payload = event.getPayload();

        log.debug("Извлечение данных для типа события: {}, класс: {}",
                eventType, payload.getClass().getSimpleName());

        return payload;
    }

    private String getEventType(SensorEventAvro sensorEvent) {
        Object payload = sensorEvent.getPayload();

        switch (payload) {
            case ClimateSensorAvro climateSensorAvro -> {
                return "CLIMATE_SENSOR_EVENT";
            }
            case LightSensorAvro lightSensorAvro -> {
                return "LIGHT_SENSOR_EVENT";
            }
            case MotionSensorAvro motionSensorAvro -> {
                return "MOTION_SENSOR_EVENT";
            }
            case SwitchSensorAvro switchSensorAvro -> {
                return "SWITCH_SENSOR_EVENT";
            }
            case TemperatureSensorAvro temperatureSensorAvro -> {
                return "TEMPERATURE_SENSOR_EVENT";
            }
            case null, default -> {
                log.warn("Неизвестный тип события: {}", payload.getClass().getSimpleName());
                return "UNKNOWN";
            }
        }
    }

    private boolean checkCondition(Condition condition, Object sensorData) {
        int conditionValue = condition.getValue();

        log.debug("Проверка условия: тип={}, операция={}, значение={}, данные={}",
                condition.getType(), condition.getOperation(), conditionValue,
                sensorData.getClass().getSimpleName());

        switch (condition.getType()) {
            case TEMPERATURE:
                Integer temperature = extractTemperature(sensorData);
                if (temperature != null) {
                    return compareValues(temperature, conditionValue, condition.getOperation());
                }
                break;

            case MOTION:
                if (sensorData instanceof MotionSensorAvro) {
                    boolean motion = ((MotionSensorAvro) sensorData).getMotion();
                    boolean expectedMotion = conditionValue == 1;
                    return motion == expectedMotion;
                }
                break;

            case LUMINOSITY:
                if (sensorData instanceof LightSensorAvro) {
                    int luminosity = ((LightSensorAvro) sensorData).getLuminosity();
                    return compareValues(luminosity, conditionValue, condition.getOperation());
                }
                break;

            case SWITCH:
                if (sensorData instanceof SwitchSensorAvro) {
                    boolean state = ((SwitchSensorAvro) sensorData).getState();
                    boolean expectedState = conditionValue == 1;
                    return state == expectedState;
                }
                break;

            case CO2LEVEL:
                Integer co2Level = extractCo2Level(sensorData);
                if (co2Level != null) {
                    return compareValues(co2Level, conditionValue, condition.getOperation());
                }
                break;

            case HUMIDITY:
                Integer humidity = extractHumidity(sensorData);
                if (humidity != null) {
                    return compareValues(humidity, conditionValue, condition.getOperation());
                }
                break;

            default:
                log.warn("Неизвестный тип условия: {}", condition.getType());
                return false;
        }

        log.warn("Несовместимый тип данных для условия {}: {}",
                condition.getType(), sensorData.getClass().getSimpleName());
        return false;
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
        if (sensorData instanceof ClimateSensorAvro) {
            return ((ClimateSensorAvro) sensorData).getCo2Level();
        }
        return null;
    }

    private Integer extractHumidity(Object sensorData) {
        if (sensorData instanceof ClimateSensorAvro) {
            return ((ClimateSensorAvro) sensorData).getHumidity();
        }
        return null;
    }

    private boolean compareValues(int actual, int expected, ConditionOperation operation) {
        return switch (operation) {
            case EQUALS -> actual == expected;
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
            default -> {
                log.warn("Неизвестная операция сравнения: {}", operation);
                yield false;
            }
        };
    }

    private void executeScenarioActions(Scenario scenario) {
        List<ScenarioAction> scenarioActions = scenarioActionRepository.findByScenarioId(scenario.getId());

        for (ScenarioAction scenarioAction : scenarioActions) {
            Action action = scenarioAction.getAction();
            Sensor sensor = scenarioAction.getSensor();

            DeviceActionAvro actionAvro = DeviceActionAvro.newBuilder()
                    .setSensorId(sensor.getId())
                    .setType(convertActionTypeToAvro(action.getType()))
                    .setValue(action.getValue())
                    .build();

            try {
                grpcCommandService.sendDeviceAction(scenario.getHubId(), scenario.getName(), actionAvro);
                log.info("Действие отправлено для сценария '{}', устройство: {}",
                        scenario.getName(), sensor.getId());
            } catch (Exception e) {
                log.error("Ошибка отправки действия для сценария '{}', устройство: {}",
                        scenario.getName(), sensor.getId(), e);
            }
        }
    }

    private ActionTypeAvro convertActionTypeToAvro(ActionType type) {
        return ActionTypeAvro.valueOf(type.name());
    }
}