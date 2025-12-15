package ru.yandex.practicum.commerce.interaction.dto.delivery;

import lombok.Data;
import ru.yandex.practicum.commerce.interaction.dto.AddressDto;
import ru.yandex.practicum.commerce.interaction.enums.DeliveryState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DeliveryDto {
    private UUID deliveryId;
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private UUID orderId;
    private DeliveryState deliveryState;
    private BigDecimal deliveryCost;
    private Integer estimatedDeliveryTime;
}