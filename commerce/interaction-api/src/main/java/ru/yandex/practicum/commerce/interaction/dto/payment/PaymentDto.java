package ru.yandex.practicum.commerce.interaction.dto.payment;

import lombok.Data;
import ru.yandex.practicum.commerce.interaction.enums.PaymentState;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentDto {
    private UUID paymentId;
    private UUID orderId;
    private PaymentState state;
    private BigDecimal productTotal;
    private BigDecimal deliveryTotal;
    private BigDecimal feeTotal;
    private BigDecimal totalPayment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}