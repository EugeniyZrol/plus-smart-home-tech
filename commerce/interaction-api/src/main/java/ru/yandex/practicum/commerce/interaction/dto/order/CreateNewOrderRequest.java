package ru.yandex.practicum.commerce.interaction.dto.order;

import lombok.Data;
import ru.yandex.practicum.commerce.interaction.dto.AddressDto;
import ru.yandex.practicum.commerce.interaction.dto.shoppingcart.ShoppingCartDto;

import jakarta.validation.constraints.NotNull;

@Data
public class CreateNewOrderRequest {
    @NotNull
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto deliveryAddress;
}