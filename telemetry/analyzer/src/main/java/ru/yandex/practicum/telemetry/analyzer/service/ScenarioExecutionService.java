package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioConditionRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioExecutionService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final GrpcCommandService grpcCommandService;
    private final ConditionComparisonService conditionComparisonService;

    @Transactional(readOnly = true)
    public void executeScenariosForSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        if (scenarios.isEmpty()) {
            return;
        }

        for (Scenario scenario : scenarios) {
            if (areConditionsMet(scenario, snapshot)) {
                log.info("Scenario conditions met: '{}' for hub: {}", scenario.getName(), hubId);
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
            String sensorId = scenarioCondition.getSensor().getId();

            SensorStateAvro sensorState = snapshot.getSensorsState().get(sensorId);
            if (sensorState == null) {
                return false;
            }

            if (!conditionComparisonService.isConditionMet(scenarioCondition.getCondition(), sensorState.getData())) {
                return false;
            }
        }

        return true;
    }

    private void executeScenarioActions(Scenario scenario) {
        List<ScenarioAction> scenarioActions = scenario.getActions();

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
            } catch (Exception e) {
                log.error("Error sending action for scenario: '{}', device: {}",
                        scenario.getName(), sensor.getId(), e);
            }
        }
    }
}