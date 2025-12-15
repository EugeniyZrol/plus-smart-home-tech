package ru.yandex.practicum.commerce.warehouse.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import ru.yandex.practicum.commerce.interaction.enums.WarehouseOrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "warehouse", name = "order_bookings")
public class OrderBooking {

    @Id
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "assembled_at")
    private LocalDateTime assembledAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WarehouseOrderStatus status;
}