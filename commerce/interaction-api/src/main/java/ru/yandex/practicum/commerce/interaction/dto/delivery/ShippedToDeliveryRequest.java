package ru.yandex.practicum.commerce.interaction.dto.delivery;

import lombok.Data;
import java.util.UUID;

@Data
public class ShippedToDeliveryRequest {
    private UUID orderId;
    private UUID deliveryId;
}