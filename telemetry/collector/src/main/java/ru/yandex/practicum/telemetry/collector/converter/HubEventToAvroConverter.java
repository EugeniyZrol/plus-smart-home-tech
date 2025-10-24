package ru.yandex.practicum.telemetry.collector.converter;

import ru.yandex.practicum.telemetry.collector.event.dto.*;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HubEventToAvroConverter {

    public HubEventAvro convert(HubEvent event) {
        HubEventAvro.Builder builder = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        switch (event.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;
                DeviceAddedEventAvro deviceAdded = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name()))
                        .build();
                builder.setPayload(deviceAdded);
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
                DeviceRemovedEventAvro deviceRemoved = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemovedEvent.getId())
                        .build();
                builder.setPayload(deviceRemoved);
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;

                List<ScenarioConditionAvro> conditions = scenarioAddedEvent.getConditions().stream()
                        .map(this::convertCondition)
                        .collect(Collectors.toList());

                List<DeviceActionAvro> actions = scenarioAddedEvent.getActions().stream()
                        .map(this::convertAction)
                        .collect(Collectors.toList());

                ScenarioAddedEventAvro scenarioAdded = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAddedEvent.getName())
                        .setConditions(conditions)
                        .setActions(actions)
                        .build();
                builder.setPayload(scenarioAdded);
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
                ScenarioRemovedEventAvro scenarioRemoved = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build();
                builder.setPayload(scenarioRemoved);
            }
        }

        return builder.build();
    }

    private ScenarioConditionAvro convertCondition(ScenarioCondition condition) {
        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()));

        if (condition.getValue() != null) {
            builder.setValue(condition.getValue());
        }

        return builder.build();
    }

    private DeviceActionAvro convertAction(DeviceAction action) {
        DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()));

        if (action.getValue() != null) {
            builder.setValue(action.getValue());
        }

        return builder.build();
    }
}