package ru.yandex.practicum.commerce.interaction.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

@Data
public class AddProductToWarehouseRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private Integer quantity;
}
