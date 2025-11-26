package ru.yandex.practicum.telemetry.collector.converter.hub;

import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.telemetry.collector.event.dto.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.ScenarioCondition;
import ru.yandex.practicum.telemetry.collector.event.dto.DeviceAction;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedConverter implements HubEventConverter {

    @Override
    public boolean canConvert(HubEventProto.PayloadCase payloadCase) {
        return payloadCase == HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubEvent convert(HubEventProto proto) {
        ScenarioAddedEvent event = new ScenarioAddedEvent();
        setBaseFields(event, proto);

        var scenarioAdded = proto.getScenarioAdded();
        event.setName(scenarioAdded.getName());

        List<ScenarioCondition> conditions = scenarioAdded.getConditionList().stream()
                .map(this::convertCondition)
                .collect(Collectors.toList());
        event.setConditions(conditions);

        List<DeviceAction> actions = scenarioAdded.getActionList().stream()
                .map(this::convertAction)
                .collect(Collectors.toList());
        event.setActions(actions);

        return event;
    }

    private void setBaseFields(HubEvent event, HubEventProto proto) {
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }

    private ScenarioCondition convertCondition(ScenarioConditionProto proto) {
        ScenarioCondition condition = new ScenarioCondition();
        condition.setSensorId(proto.getSensorId());
        condition.setType(ru.yandex.practicum.telemetry.collector.event.enums.ConditionType.valueOf(
                proto.getType().name()));
        condition.setOperation(ru.yandex.practicum.telemetry.collector.event.enums.ConditionOperation.valueOf(
                proto.getOperation().name()));

        switch (proto.getValueCase()) {
            case BOOL_VALUE:
                condition.setValue(proto.getBoolValue() ? 1 : 0);
                break;
            case INT_VALUE:
                condition.setValue(proto.getIntValue());
                break;
            case VALUE_NOT_SET:
                break;
        }
        return condition;
    }

    private DeviceAction convertAction(DeviceActionProto proto) {
        DeviceAction action = new DeviceAction();
        action.setSensorId(proto.getSensorId());
        action.setType(ru.yandex.practicum.telemetry.collector.event.enums.ActionType.valueOf(
                proto.getType().name()));

        if (proto.hasValue()) {
            action.setValue(proto.getValue());
        }
        return action;
    }
}