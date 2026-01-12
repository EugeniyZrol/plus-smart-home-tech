package ru.yandex.practicum.commerce.interaction.dto.shoppingstore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction.enums.ProductState;
import ru.yandex.practicum.commerce.interaction.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDto {
    private UUID productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private ProductCategory productCategory;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState = ProductState.ACTIVE;
}