package ru.yandex.practicum.commerce.payment.service;

import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto);

    BigDecimal calculateTotalCost(OrderDto orderDto);

    void markPaymentAsSuccess(UUID paymentId);

    BigDecimal calculateProductCost(OrderDto orderDto);

    void markPaymentAsFailed(UUID paymentId);

    PaymentDto getPayment(UUID paymentId);
}