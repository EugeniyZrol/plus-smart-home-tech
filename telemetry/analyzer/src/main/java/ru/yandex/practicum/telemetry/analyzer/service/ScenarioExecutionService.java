package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.repository.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioExecutionService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final GrpcCommandService grpcCommandService;

    @Transactional(readOnly = true)
    public void executeScenariosForSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        if (scenarios.isEmpty()) {
            log.debug("Нет сценариев для хаба: {}", hubId);
            return;
        }

        log.debug("Проверка {} сценариев для хаба: {}", scenarios.size(), hubId);

        for (Scenario scenario : scenarios) {
            if (areConditionsMet(scenario, snapshot)) {
                log.info("Условия сценария '{}' выполнены для хаба: {}", scenario.getName(), hubId);
                executeScenarioActions(scenario);
            }
        }
    }

    private boolean areConditionsMet(Scenario scenario, SensorsSnapshotAvro snapshot) {
        List<ScenarioCondition> conditions = scenarioConditionRepository.findByScenarioIdWithCondition(scenario.getId());

        if (conditions.isEmpty()) {
            return false;
        }

        for (ScenarioCondition scenarioCondition : conditions) {
            Condition condition = scenarioCondition.getCondition();
            String sensorId = scenarioCondition.getSensor().getId();

            SensorStateAvro sensorState = snapshot.getSensorsState().get(sensorId);
            if (sensorState == null) {
                log.debug("Датчик {} не найден в снапшоте хаба {}", sensorId, snapshot.getHubId());
                return false;
            }

            if (!checkCondition(condition, sensorState.getData())) {
                log.debug("Условие не выполнено для датчика {}: {} {} {}",
                        sensorId, condition.getType(), condition.getOperation(), condition.getValue());
                return false;
            }
        }

        return true;
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

    private boolean compareValues(int actual, int expected, ConditionOperationAvro operation) {
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
        List<ScenarioAction> scenarioActions = scenarioActionRepository.findByScenarioIdWithAction(scenario.getId());

        for (ScenarioAction scenarioAction : scenarioActions) {
            Action action = scenarioAction.getAction();
            Sensor sensor = scenarioAction.getSensor();

            DeviceActionAvro actionAvro = DeviceActionAvro.newBuilder()
                    .setSensorId(sensor.getId())
                    .setType(action.getType())
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
}