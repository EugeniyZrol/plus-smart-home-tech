package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.repository.*;
import ru.yandex.practicum.telemetry.collector.event.enums.ConditionType;
import ru.yandex.practicum.telemetry.collector.event.enums.ConditionOperation;
import ru.yandex.practicum.telemetry.collector.event.enums.ActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioStorageService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    @Transactional
    public void addDevice(String hubId, DeviceAddedEventAvro device) {
        Optional<Sensor> existingSensor = sensorRepository.findById(device.getId());
        if (existingSensor.isPresent()) {
            Sensor sensor = existingSensor.get();
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
            log.info("Обновлено устройство '{}' для хаба: {}", device.getId(), hubId);
        } else {
            Sensor sensor = new Sensor(device.getId(), hubId);
            sensorRepository.save(sensor);
            log.info("Добавлено устройство '{}' для хаба: {}", device.getId(), hubId);
        }
    }

    @Transactional
    public void removeDevice(String hubId, DeviceRemovedEventAvro device) {
        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(device.getId(), hubId);
        if (sensor.isPresent()) {
            scenarioConditionRepository.deleteBySensorId(device.getId());
            scenarioActionRepository.deleteBySensorId(device.getId());

            sensorRepository.delete(sensor.get());
            log.info("Удалено устройство '{}' для хаба: {}", device.getId(), hubId);
        } else {
            log.warn("Устройство '{}' не найдено для хаба: {}", device.getId(), hubId);
        }
    }

    @Transactional
    public void addScenario(String hubId, ScenarioAddedEventAvro scenarioAvro) {
        Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(hubId, scenarioAvro.getName());
        if (existingScenario.isPresent()) {
            updateScenario(existingScenario.get(), scenarioAvro);
        } else {
            createNewScenario(hubId, scenarioAvro);
        }
        log.info("Сценарий '{}' сохранен для хаба: {}", scenarioAvro.getName(), hubId);
    }

    private void createNewScenario(String hubId, ScenarioAddedEventAvro scenarioAvro) {
        Scenario scenario = new Scenario(hubId, scenarioAvro.getName());
        scenario = scenarioRepository.save(scenario);

        saveScenarioConditions(scenario, scenarioAvro.getConditions());
        saveScenarioActions(scenario, scenarioAvro.getActions());
    }

    private void updateScenario(Scenario scenario, ScenarioAddedEventAvro scenarioAvro) {
        scenarioConditionRepository.deleteByScenarioId(scenario.getId());
        scenarioActionRepository.deleteByScenarioId(scenario.getId());

        saveScenarioConditions(scenario, scenarioAvro.getConditions());
        saveScenarioActions(scenario, scenarioAvro.getActions());
    }

    private void saveScenarioConditions(Scenario scenario, List<ScenarioConditionAvro> conditionsAvro) {
        for (ScenarioConditionAvro conditionAvro : conditionsAvro) {
            Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(
                    conditionAvro.getSensorId(), scenario.getHubId());

            if (sensor.isEmpty()) {
                log.warn("Сенсор {} не найден для хаба {}, пропускаем условие",
                        conditionAvro.getSensorId(), scenario.getHubId());
                continue;
            }

            ConditionType type = convertConditionType(conditionAvro.getType());
            ConditionOperation operation = convertConditionOperation(conditionAvro.getOperation());

            Condition condition = new Condition(
                    type,
                    operation,
                    conditionAvro.getValue()
            );
            condition = conditionRepository.save(condition);

            ScenarioCondition scenarioCondition = new ScenarioCondition();
            scenarioCondition.setScenario(scenario);
            scenarioCondition.setSensor(sensor.get());
            scenarioCondition.setCondition(condition);

            scenarioConditionRepository.save(scenarioCondition);
        }
    }

    private void saveScenarioActions(Scenario scenario, List<DeviceActionAvro> actionsAvro) {
        for (DeviceActionAvro actionAvro : actionsAvro) {
            Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(
                    actionAvro.getSensorId(), scenario.getHubId());

            if (sensor.isEmpty()) {
                log.warn("Сенсор {} не найден для хаба {}, пропускаем действие",
                        actionAvro.getSensorId(), scenario.getHubId());
                continue;
            }

            ActionType type = convertActionType(actionAvro.getType());

            Action action = new Action(
                    type,
                    actionAvro.getValue()
            );
            action = actionRepository.save(action);

            ScenarioAction scenarioAction = new ScenarioAction();
            scenarioAction.setScenario(scenario);
            scenarioAction.setSensor(sensor.get());
            scenarioAction.setAction(action);

            scenarioActionRepository.save(scenarioAction);
        }
    }

    @Transactional
    public void removeScenario(String hubId, ScenarioRemovedEventAvro scenario) {
        Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(hubId, scenario.getName());
        if (existingScenario.isPresent()) {
            scenarioConditionRepository.deleteByScenarioId(existingScenario.get().getId());
            scenarioActionRepository.deleteByScenarioId(existingScenario.get().getId());

            scenarioRepository.delete(existingScenario.get());
            log.info("Удален сценарий '{}' для хаба: {}", scenario.getName(), hubId);
        } else {
            log.warn("Сценарий '{}' не найден для хаба: {}", scenario.getName(), hubId);
        }
    }

    @Transactional(readOnly = true)
    public List<ScenarioAddedEventAvro> getScenariosForHub(String hubId) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        List<ScenarioAddedEventAvro> result = new ArrayList<>();

        for (Scenario scenario : scenarios) {
            List<ScenarioCondition> conditions = scenarioConditionRepository.findByScenarioId(scenario.getId());
            List<ScenarioAction> actions = scenarioActionRepository.findByScenarioId(scenario.getId());

            result.add(convertToAvro(scenario, conditions, actions));
        }

        return result;
    }

    private ScenarioAddedEventAvro convertToAvro(Scenario scenario, List<ScenarioCondition> conditions, List<ScenarioAction> actions) {
        List<ScenarioConditionAvro> conditionsAvro = new ArrayList<>();
        for (ScenarioCondition sc : conditions) {
            Condition condition = sc.getCondition();
            Sensor sensor = sc.getSensor();

            ConditionTypeAvro typeAvro = convertConditionTypeToAvro(condition.getType());
            ConditionOperationAvro operationAvro = convertConditionOperationToAvro(condition.getOperation());

            ScenarioConditionAvro conditionAvro = ScenarioConditionAvro.newBuilder()
                    .setSensorId(sensor.getId())
                    .setType(typeAvro)
                    .setOperation(operationAvro)
                    .setValue(condition.getValue())
                    .build();
            conditionsAvro.add(conditionAvro);
        }

        List<DeviceActionAvro> actionsAvro = new ArrayList<>();
        for (ScenarioAction sa : actions) {
            Action action = sa.getAction();
            Sensor sensor = sa.getSensor();

            ActionTypeAvro typeAvro = convertActionTypeToAvro(action.getType());

            DeviceActionAvro actionAvro = DeviceActionAvro.newBuilder()
                    .setSensorId(sensor.getId())
                    .setType(typeAvro)
                    .setValue(action.getValue())
                    .build();
            actionsAvro.add(actionAvro);
        }

        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenario.getName())
                .setConditions(conditionsAvro)
                .setActions(actionsAvro)
                .build();
    }

    private ConditionType convertConditionType(ConditionTypeAvro avroType) {
        return ConditionType.valueOf(avroType.name());
    }

    private ConditionOperation convertConditionOperation(ConditionOperationAvro avroOperation) {
        return ConditionOperation.valueOf(avroOperation.name());
    }

    private ActionType convertActionType(ActionTypeAvro avroType) {
        return ActionType.valueOf(avroType.name());
    }

    private ConditionTypeAvro convertConditionTypeToAvro(ConditionType type) {
        return ConditionTypeAvro.valueOf(type.name());
    }

    private ConditionOperationAvro convertConditionOperationToAvro(ConditionOperation operation) {
        return ConditionOperationAvro.valueOf(operation.name());
    }

    private ActionTypeAvro convertActionTypeToAvro(ActionType type) {
        return ActionTypeAvro.valueOf(type.name());
    }
}