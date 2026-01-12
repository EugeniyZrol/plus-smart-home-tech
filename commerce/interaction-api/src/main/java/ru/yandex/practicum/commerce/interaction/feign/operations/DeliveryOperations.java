package ru.yandex.practicum.commerce.interaction.feign.operations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOperations {

    ResponseEntity<DeliveryDto> planDelivery(DeliveryDto deliveryDto);

    ResponseEntity<Void> deliverySuccessful(UUID orderId);

    ResponseEntity<Void> deliveryPicked(UUID orderId);

    ResponseEntity<Void> deliveryFailed(UUID orderId);

    ResponseEntity<BigDecimal> deliveryCost(OrderDto orderDto);

    ResponseEntity<DeliveryDto> getDelivery(@PathVariable("deliveryId") UUID deliveryId);

}