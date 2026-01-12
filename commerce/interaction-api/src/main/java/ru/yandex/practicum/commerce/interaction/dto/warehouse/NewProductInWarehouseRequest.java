package ru.yandex.practicum.commerce.interaction.dto.warehouse;

import lombok.Data;
import ru.yandex.practicum.commerce.interaction.dto.DimensionDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

@Data
public class NewProductInWarehouseRequest {
    @NotNull
    private UUID productId;

    private Boolean fragile = false;

    @NotNull
    private DimensionDto dimension;

    @NotNull
    @Positive
    private Double weight;
}