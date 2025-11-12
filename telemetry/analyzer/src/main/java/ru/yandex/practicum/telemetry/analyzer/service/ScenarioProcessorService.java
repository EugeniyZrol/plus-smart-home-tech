package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioConditionRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioProcessorService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final GrpcCommandService grpcCommandService;
    private final SensorStateService sensorStateService;
    private final ConditionComparisonService conditionComparisonService;

    @Transactional
    public void processSensorEvent(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        sensorStateService.updateSensorState(hubId, sensorId, event.getPayload());

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        for (Scenario scenario : scenarios) {
            if (isScenarioTriggered(scenario, event, sensorId)) {
                log.info("Scenario activated: '{}' by sensor {}", scenario.getName(), sensorId);
                executeScenarioActions(scenario);
            }
        }
    }

    private boolean isScenarioTriggered(Scenario scenario, SensorEventAvro event, String triggeringSensorId) {
        List<ScenarioCondition> allConditions = scenarioConditionRepository.findByScenarioId(scenario.getId());

        if (allConditions.isEmpty()) {
            return false;
        }

        for (ScenarioCondition scenarioCondition : allConditions) {
            Condition condition = scenarioCondition.getCondition();
            String conditionSensorId = scenarioCondition.getSensor().getId();

            Object sensorData = getSensorDataForCondition(scenario.getHubId(), conditionSensorId, event, triggeringSensorId);

            if (sensorData == null) {
                return false;
            }

            if (!conditionComparisonService.isConditionMet(condition, sensorData)) {
                return false;
            }
        }

        return true;
    }

    private Object getSensorDataForCondition(String hubId, String conditionSensorId, SensorEventAvro currentEvent, String triggeringSensorId) {
        if (conditionSensorId.equals(triggeringSensorId)) {
            return currentEvent.getPayload();
        }
        return sensorStateService.getSensorState(hubId, conditionSensorId);
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