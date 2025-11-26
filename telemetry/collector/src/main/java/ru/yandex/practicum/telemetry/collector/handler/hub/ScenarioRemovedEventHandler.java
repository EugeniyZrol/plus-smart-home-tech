package ru.yandex.practicum.telemetry.collector.handler.hub;

import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import ru.yandex.practicum.telemetry.collector.event.dto.ScenarioRemovedEvent;
import ru.yandex.practicum.telemetry.collector.producer.HubEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final HubEventProducer hubEventProducer;

    @Override
    public boolean canHandle(HubEventProto.PayloadCase payloadCase) {
        return payloadCase == HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEvent event) {
        ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
        log.info("Обработка события удаления сценария: {} из хаба {}",
                scenarioRemovedEvent.getName(), scenarioRemovedEvent.getHubId());

        hubEventProducer.sendHubEvent(scenarioRemovedEvent);
    }
}