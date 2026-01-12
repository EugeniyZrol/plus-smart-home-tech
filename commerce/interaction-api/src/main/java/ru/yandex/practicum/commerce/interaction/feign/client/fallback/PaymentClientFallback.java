package ru.yandex.practicum.commerce.interaction.feign.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interaction.enums.PaymentState;
import ru.yandex.practicum.commerce.interaction.feign.operations.PaymentOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class PaymentClientFallback implements PaymentOperations {

    @Override
    public ResponseEntity<PaymentDto> payment(OrderDto orderDto) {
        log.warn("Сервис оплаты недоступен - fallback для создания платежа");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<BigDecimal> getTotalCost(OrderDto orderDto) {
        log.warn("Сервис оплаты недоступен - fallback для расчета общей стоимости");
        // Возвращаем дефолтную стоимость
        return ResponseEntity.ok(new BigDecimal("1000.00"));
    }

    @Override
    public ResponseEntity<Void> paymentSuccess(UUID paymentId) {
        log.warn("Сервис оплаты недоступен - fallback для успешной оплаты");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<BigDecimal> productCost(OrderDto orderDto) {
        log.warn("Сервис оплаты недоступен - fallback для расчета стоимости товаров");
        // Возвращаем дефолтную стоимость товаров
        return ResponseEntity.ok(new BigDecimal("500.00"));
    }

    @Override
    public ResponseEntity<Void> paymentFailed(UUID paymentId) {
        log.warn("Сервис оплаты недоступен - fallback для неудачной оплаты");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<PaymentDto> getPayment(UUID paymentId) {
        log.warn("Сервис оплаты недоступен - fallback для получения платежа");

        PaymentDto fallbackPayment = new PaymentDto();
        fallbackPayment.setPaymentId(paymentId);
        fallbackPayment.setState(PaymentState.PENDING);
        fallbackPayment.setTotalPayment(new BigDecimal("1000.00"));
        fallbackPayment.setCreatedAt(LocalDateTime.now());
        fallbackPayment.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(fallbackPayment);
    }
}