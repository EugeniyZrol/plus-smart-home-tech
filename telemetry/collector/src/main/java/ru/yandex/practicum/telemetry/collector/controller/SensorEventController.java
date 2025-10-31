package ru.yandex.practicum.telemetry.collector.controller;

import ru.yandex.practicum.telemetry.collector.event.dto.SensorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.collector.producer.SensorEventProducer;

@RestController
@RequestMapping("/events/sensors")
@RequiredArgsConstructor
public class SensorEventController {

    private final SensorEventProducer sensorEventProducer;

    @PostMapping
    public ResponseEntity<String> sendSensorEvent(@RequestBody SensorEvent event) {
        sensorEventProducer.sendSensorEvent(event);
        return ResponseEntity.ok("Сенсорное событие отправлено успешно");
    }
}