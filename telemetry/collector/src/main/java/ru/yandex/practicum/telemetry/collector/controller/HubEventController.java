package ru.yandex.practicum.telemetry.collector.controller;

import ru.yandex.practicum.telemetry.collector.event.dto.HubEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.producer.HubEventProducer;

@RestController
@RequestMapping("/events/hubs")
@RequiredArgsConstructor
public class HubEventController {

    private final HubEventProducer hubEventProducer;

    @PostMapping
    public ResponseEntity<String> sendHubEvent(@RequestBody HubEvent event) {
        hubEventProducer.sendHubEvent(event);
        return ResponseEntity.ok("Хаб событие отправлено успешно");
    }
}