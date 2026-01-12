package ru.yandex.practicum.commerce.delivery.service;

import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    void markDeliveryAsSuccessful(UUID orderId);

    void markDeliveryAsPicked(UUID orderId);

    void markDeliveryAsFailed(UUID orderId);

    BigDecimal calculateDeliveryCost(OrderDto orderDto);

    DeliveryDto getDelivery(UUID deliveryId);
}