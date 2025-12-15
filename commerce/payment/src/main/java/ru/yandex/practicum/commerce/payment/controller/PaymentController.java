package ru.yandex.practicum.commerce.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interaction.feign.operations.PaymentOperations;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {

    private final PaymentService paymentService;

    @Override
    @PostMapping
    public ResponseEntity<PaymentDto> payment(@Valid @RequestBody OrderDto orderDto) {
        PaymentDto payment = paymentService.createPayment(orderDto);
        return ResponseEntity.ok(payment);
    }

    @Override
    @PostMapping("/totalCost")
    public ResponseEntity<BigDecimal> getTotalCost(@Valid @RequestBody OrderDto orderDto) {
        BigDecimal totalCost = paymentService.calculateTotalCost(orderDto);
        return ResponseEntity.ok(totalCost);
    }

    @Override
    @PostMapping("/refund")
    public ResponseEntity<Void> paymentSuccess(@RequestBody UUID paymentId) {
        paymentService.markPaymentAsSuccess(paymentId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/productCost")
    public ResponseEntity<BigDecimal> productCost(@Valid @RequestBody OrderDto orderDto) {
        BigDecimal productCost = paymentService.calculateProductCost(orderDto);
        return ResponseEntity.ok(productCost);
    }

    @Override
    @PostMapping("/failed")
    public ResponseEntity<Void> paymentFailed(@RequestBody UUID paymentId) {
        paymentService.markPaymentAsFailed(paymentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable UUID paymentId) {
        PaymentDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }
}