package ru.yandex.practicum.commerce.interaction.feign.operations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {

    ResponseEntity<PaymentDto> payment(OrderDto orderDto);

    ResponseEntity<BigDecimal> getTotalCost(OrderDto orderDto);

    ResponseEntity<Void> paymentSuccess(UUID paymentId);

    ResponseEntity<BigDecimal> productCost(OrderDto orderDto);

    ResponseEntity<Void> paymentFailed(UUID paymentId);

    ResponseEntity<PaymentDto> getPayment(@PathVariable("paymentId") UUID paymentId);
}