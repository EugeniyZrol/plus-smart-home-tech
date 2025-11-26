package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.config.ScenarioProperties;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.entity.Condition;
import ru.yandex.practicum.telemetry.analyzer.repository.*;

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
    private final ScenarioProperties scenarioProperties;

    @Transactional
    public void addDevice(String hubId, DeviceAddedEventAvro device) {
        Optional<Sensor> existingSensor = sensorRepository.findById(device.getId());
        if (existingSensor.isPresent()) {
            Sensor sensor = existingSensor.get();
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
            log.debug("Device updated: '{}' for hub: {}", device.getId(), hubId);
        } else {
            Sensor sensor = new Sensor(device.getId(), hubId);
            sensorRepository.save(sensor);
            log.debug("Device added: '{}' for hub: {}", device.getId(), hubId);
        }
    }

    @Transactional
    public void removeDevice(String hubId, DeviceRemovedEventAvro device) {
        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(device.getId(), hubId);
        if (sensor.isPresent()) {
            scenarioConditionRepository.deleteBySensorId(device.getId());
            scenarioActionRepository.deleteBySensorId(device.getId());
            sensorRepository.delete(sensor.get());
            log.debug("Device removed: '{}' for hub: {}", device.getId(), hubId);
        }
    }

    @Transactional
    public void addScenario(String hubId, ScenarioAddedEventAvro scenarioAvro) {
        if (scenarioProperties.getSpecialScenarios().contains(scenarioAvro.getName())) {
            log.info("Adding special scenario: {}", scenarioAvro.getName());
        }

        Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(hubId, scenarioAvro.getName());
        if (existingScenario.isPresent()) {
            updateScenario(existingScenario.get(), scenarioAvro);
        } else {
            createNewScenario(hubId, scenarioAvro);
        }
        log.debug("Scenario saved: '{}' for hub: {}", scenarioAvro.getName(), hubId);
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
                continue;
            }

            Condition condition = new Condition(
                    conditionAvro.getType(),
                    conditionAvro.getOperation(),
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
                continue;
            }

            Action action = new Action(
                    actionAvro.getType(),
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
            log.debug("Scenario removed: '{}' for hub: {}", scenario.getName(), hubId);
        }
    }
}