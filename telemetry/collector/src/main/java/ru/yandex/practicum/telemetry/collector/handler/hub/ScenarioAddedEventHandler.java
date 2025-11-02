package ru.yandex.practicum.telemetry.collector.handler.hub;

import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.producer.HubEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final HubEventProducer hubEventProducer;

    @Override
    public boolean canHandle(HubEventProto.PayloadCase payloadCase) {
        return payloadCase == HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
        log.info("Обработка события добавления сценария: {} в хабе {} (условий: {}, действий: {})",
                scenarioAddedEvent.getName(), scenarioAddedEvent.getHubId(),
                scenarioAddedEvent.getConditions().size(), scenarioAddedEvent.getActions().size());

        hubEventProducer.sendHubEvent(scenarioAddedEvent);
    }
}