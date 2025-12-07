package ru.yandex.practicum.commerce.interaction.dto;

import lombok.Data;

@Data
public class BookedProductsDto {
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
}
