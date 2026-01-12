package ru.yandex.practicum.commerce.interaction.dto.order;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Data
public class ProductReturnRequest {
    private UUID orderId;

    @NotNull
    private Map<UUID, Integer> products;
}