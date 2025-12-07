package ru.yandex.practicum.commerce.interaction.dto;

import lombok.Data;
import ru.yandex.practicum.commerce.interaction.enums.QuantityState;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}