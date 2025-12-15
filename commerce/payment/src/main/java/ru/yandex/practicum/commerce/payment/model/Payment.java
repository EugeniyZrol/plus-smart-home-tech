package ru.yandex.practicum.commerce.payment.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.commerce.interaction.enums.PaymentState;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payments", schema = "payment")
public class Payment {

    @Id
    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private PaymentState state;

    @Column(name = "product_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal productTotal;

    @Column(name = "delivery_total", precision = 10, scale = 2)
    private BigDecimal deliveryTotal;

    @Column(name = "fee_total", precision = 10, scale = 2)
    private BigDecimal feeTotal;

    @Column(name = "total_payment", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPayment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", state=" + state +
                ", totalPayment=" + totalPayment +
                '}';
    }
}