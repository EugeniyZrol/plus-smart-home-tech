package ru.yandex.practicum.commerce.interaction.feign.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interaction.feign.client.fallback.PaymentClientFallback;
import ru.yandex.practicum.commerce.interaction.feign.operations.PaymentOperations;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(
        name = "payment",
        fallback = PaymentClientFallback.class
)
@CircuitBreaker(name = "payment")
public interface PaymentClient extends PaymentOperations {

    @Override
    @PostMapping("/api/v1/payment")
    ResponseEntity<PaymentDto> payment(@RequestBody OrderDto orderDto);

    @Override
    @PostMapping("/api/v1/payment/totalCost")
    ResponseEntity<BigDecimal> getTotalCost(@RequestBody OrderDto orderDto);

    @Override
    @PostMapping("/api/v1/payment/refund")
    ResponseEntity<Void> paymentSuccess(@RequestBody UUID paymentId);

    @Override
    @PostMapping("/api/v1/payment/productCost")
    ResponseEntity<BigDecimal> productCost(@RequestBody OrderDto orderDto);

    @Override
    @PostMapping("/api/v1/payment/failed")
    ResponseEntity<Void> paymentFailed(@RequestBody UUID paymentId);

    @Override
    @GetMapping("/api/v1/payment/{paymentId}")
    ResponseEntity<PaymentDto> getPayment(@PathVariable("paymentId") UUID paymentId);
}