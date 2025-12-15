package ru.yandex.practicum.commerce.interaction.dto.order;

import lombok.Data;
import ru.yandex.practicum.commerce.interaction.dto.AddressDto;
import ru.yandex.practicum.commerce.interaction.enums.OrderState;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID orderId;
    private String username;
    private UUID shoppingCartId;
    private UUID paymentId;
    private UUID deliveryId;
    private OrderState state;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AddressDto deliveryAddress;
    private Map<UUID, Integer> products;
}