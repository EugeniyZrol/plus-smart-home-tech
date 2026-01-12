package ru.yandex.practicum.commerce.interaction.dto.warehouse;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

@Data
public class ChangeProductQuantityRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private Integer newQuantity;
}