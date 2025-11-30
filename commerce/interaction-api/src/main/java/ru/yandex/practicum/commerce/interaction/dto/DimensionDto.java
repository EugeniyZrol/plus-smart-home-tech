package ru.yandex.practicum.commerce.interaction.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class DimensionDto {
    @NotNull
    @Positive
    private Double width;

    @NotNull
    @Positive
    private Double height;

    @NotNull
    @Positive
    private Double depth;
}