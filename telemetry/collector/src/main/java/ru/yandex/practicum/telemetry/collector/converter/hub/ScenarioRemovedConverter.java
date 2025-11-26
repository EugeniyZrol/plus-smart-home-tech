package ru.yandex.practicum.telemetry.collector.converter.hub;

import ru.yandex.practicum.telemetry.collector.event.dto.ScenarioRemovedEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ScenarioRemovedConverter implements HubEventConverter {

    @Override
    public boolean canConvert(HubEventProto.PayloadCase payloadCase) {
        return payloadCase == HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public HubEvent convert(HubEventProto proto) {
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();
        setBaseFields(event, proto);

        var scenarioRemoved = proto.getScenarioRemoved();
        event.setName(scenarioRemoved.getName());

        return event;
    }

    private void setBaseFields(HubEvent event, HubEventProto proto) {
        event.setHubId(proto.getHubId());
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        event.setTimestamp(timestamp);
    }
}