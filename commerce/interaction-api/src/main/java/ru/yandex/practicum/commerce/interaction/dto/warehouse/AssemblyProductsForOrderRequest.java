package ru.yandex.practicum.commerce.interaction.dto.warehouse;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class AssemblyProductsForOrderRequest {
    private UUID orderId;
    private Map<UUID, Integer> products;
}