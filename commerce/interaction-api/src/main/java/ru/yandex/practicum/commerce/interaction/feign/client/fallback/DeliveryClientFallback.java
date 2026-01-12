package ru.yandex.practicum.commerce.interaction.feign.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.enums.DeliveryState;
import ru.yandex.practicum.commerce.interaction.feign.operations.DeliveryOperations;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class DeliveryClientFallback implements DeliveryOperations {

    @Override
    public ResponseEntity<DeliveryDto> planDelivery(DeliveryDto deliveryDto) {
        log.warn("Сервис доставки недоступен - fallback для планирования доставки");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deliverySuccessful(UUID orderId) {
        log.warn("Сервис доставки недоступен - fallback для успешной доставки");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deliveryPicked(UUID orderId) {
        log.warn("Сервис доставки недоставки недоступен - fallback для получения товара");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deliveryFailed(UUID orderId) {
        log.warn("Сервис доставки недоступен - fallback для неудачной доставки");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<BigDecimal> deliveryCost(OrderDto orderDto) {
        log.warn("Сервис доставки недоступен - fallback для расчета стоимости доставки");
        // Возвращаем дефолтную стоимость доставки
        return ResponseEntity.ok(new BigDecimal("300.00"));
    }

    @Override
    public ResponseEntity<DeliveryDto> getDelivery(UUID deliveryId) {
        log.warn("Сервис доставки недоступен - fallback для получения доставки");

        DeliveryDto fallbackDelivery = new DeliveryDto();
        fallbackDelivery.setDeliveryId(deliveryId);
        fallbackDelivery.setDeliveryState(DeliveryState.CREATED);

        return ResponseEntity.ok(fallbackDelivery);
    }
}