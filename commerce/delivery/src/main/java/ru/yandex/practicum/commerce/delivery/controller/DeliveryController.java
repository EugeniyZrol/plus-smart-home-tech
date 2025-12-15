package ru.yandex.practicum.commerce.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.feign.operations.DeliveryOperations;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryOperations {

    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public ResponseEntity<DeliveryDto> planDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        DeliveryDto createdDelivery = deliveryService.createDelivery(deliveryDto);
        return ResponseEntity.ok(createdDelivery);
    }

    @Override
    @PostMapping("/successful")
    public ResponseEntity<Void> deliverySuccessful(@RequestBody UUID orderId) {
        deliveryService.markDeliveryAsSuccessful(orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/picked")
    public ResponseEntity<Void> deliveryPicked(@RequestBody UUID orderId) {
        deliveryService.markDeliveryAsPicked(orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/failed")
    public ResponseEntity<Void> deliveryFailed(@RequestBody UUID orderId) {
        deliveryService.markDeliveryAsFailed(orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/cost")
    public ResponseEntity<BigDecimal> deliveryCost(@Valid @RequestBody OrderDto orderDto) {
        BigDecimal cost = deliveryService.calculateDeliveryCost(orderDto);
        return ResponseEntity.ok(cost);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryDto> getDelivery(@PathVariable UUID deliveryId) {
        DeliveryDto delivery = deliveryService.getDelivery(deliveryId);
        return ResponseEntity.ok(delivery);
    }
}