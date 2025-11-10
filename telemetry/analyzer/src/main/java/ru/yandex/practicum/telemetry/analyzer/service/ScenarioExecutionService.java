package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioExecutionService {

    private final ScenarioStorageService scenarioStorageService;
    private final GrpcCommandService grpcCommandService;

    public void executeScenariosForSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<ScenarioAddedEventAvro> scenarios = scenarioStorageService.getScenariosForHub(hubId);

        if (scenarios.isEmpty()) {
            log.debug("Нет сценариев для хаба: {}", hubId);
            return;
        }

        log.debug("Проверка {} сценариев для хаба: {}", scenarios.size(), hubId);

        for (ScenarioAddedEventAvro scenario : scenarios) {
            if (areConditionsMet(scenario.getConditions(), snapshot)) {
                log.info("Условия сценария '{}' выполнены для хаба: {}", scenario.getName(), hubId);
                executeActions(scenario.getName(), scenario.getActions(), hubId);
            }
        }
    }

    private boolean areConditionsMet(List<ScenarioConditionAvro> conditions, SensorsSnapshotAvro snapshot) {
        for (ScenarioConditionAvro condition : conditions) {
            if (!isConditionMet(condition, snapshot)) {
                return false;
            }
        }
        return true;
    }

    private boolean isConditionMet(ScenarioConditionAvro condition, SensorsSnapshotAvro snapshot) {
        String sensorId = condition.getSensorId();
        SensorStateAvro sensorState = snapshot.getSensorsState().get(sensorId);

        if (sensorState == null) {
            log.warn("Датчик {} не найден в снапшоте хаба {}", sensorId, snapshot.getHubId());
            return false;
        }

        Object sensorData = sensorState.getData();

        return switch (condition.getType()) {
            case TEMPERATURE -> checkTemperatureCondition(condition, sensorData);
            case MOTION -> checkMotionCondition(condition, sensorData);
            case LUMINOSITY -> checkLuminosityCondition(condition, sensorData);
            case SWITCH -> checkSwitchCondition(condition, sensorData);
            case CO2LEVEL -> checkCo2LevelCondition(condition, sensorData);
            case HUMIDITY -> checkHumidityCondition(condition, sensorData);
            default -> {
                log.warn("Неизвестный тип условия: {}", condition.getType());
                yield false;
            }
        };
    }

    private boolean checkTemperatureCondition(ScenarioConditionAvro condition, Object sensorData) {
        Integer temperature = extractTemperature(sensorData);
        if (temperature == null) {
            log.warn("Неверный тип данных для температурного условия: {}", sensorData.getClass().getSimpleName());
            return false;
        }

        int conditionValue = condition.getValue();
        return compareValues(temperature, conditionValue, condition.getOperation());
    }

    private boolean checkMotionCondition(ScenarioConditionAvro condition, Object sensorData) {
        if (!(sensorData instanceof MotionSensorAvro)) {
            log.warn("Неверный тип данных для условия движения: {}", sensorData.getClass().getSimpleName());
            return false;
        }

        MotionSensorAvro motionData = (MotionSensorAvro) sensorData;
        boolean motion = motionData.getMotion();

        boolean conditionValue = condition.getValue() == 1;

        return motion == conditionValue;
    }

    private boolean checkLuminosityCondition(ScenarioConditionAvro condition, Object sensorData) {
        if (!(sensorData instanceof LightSensorAvro)) {
            log.warn("Неверный тип данных для условия освещенности: {}", sensorData.getClass().getSimpleName());
            return false;
        }

        LightSensorAvro lightData = (LightSensorAvro) sensorData;
        int luminosity = lightData.getLuminosity();
        int conditionValue = condition.getValue();

        return compareValues(luminosity, conditionValue, condition.getOperation());
    }

    private boolean checkSwitchCondition(ScenarioConditionAvro condition, Object sensorData) {
        if (!(sensorData instanceof SwitchSensorAvro)) {
            log.warn("Неверный тип данных для условия переключателя: {}", sensorData.getClass().getSimpleName());
            return false;
        }

        SwitchSensorAvro switchData = (SwitchSensorAvro) sensorData;
        boolean state = switchData.getState();

        boolean conditionValue = condition.getValue() == 1;

        return state == conditionValue;
    }

    private boolean checkCo2LevelCondition(ScenarioConditionAvro condition, Object sensorData) {
        Integer co2Level = extractCo2Level(sensorData);
        if (co2Level == null) {
            log.warn("Неверный тип данных для условия уровня CO2: {}", sensorData.getClass().getSimpleName());
            return false;
        }

        int conditionValue = condition.getValue();
        return compareValues(co2Level, conditionValue, condition.getOperation());
    }

    private boolean checkHumidityCondition(ScenarioConditionAvro condition, Object sensorData) {
        Integer humidity = extractHumidity(sensorData);
        if (humidity == null) {
            log.warn("Неверный тип данных для условия влажности: {}", sensorData.getClass().getSimpleName());
            return false;
        }

        int conditionValue = condition.getValue();
        return compareValues(humidity, conditionValue, condition.getOperation());
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

    private void executeActions(String scenarioName, List<DeviceActionAvro> actions, String hubId) {
        for (DeviceActionAvro action : actions) {
            try {
                grpcCommandService.sendDeviceAction(hubId, scenarioName, action);
                log.info("Действие отправлено для сценария '{}', устройство: {}, тип: {}",
                        scenarioName, action.getSensorId(), action.getType());
            } catch (Exception e) {
                log.error("Ошибка отправки действия для сценария '{}', устройство: {}",
                        scenarioName, action.getSensorId(), e);
            }
        }
    }
}