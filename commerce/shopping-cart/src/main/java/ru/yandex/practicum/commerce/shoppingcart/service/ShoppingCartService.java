package ru.yandex.practicum.commerce.shoppingcart.service;

import ru.yandex.practicum.commerce.interaction.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> productsToAdd);

    ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIdsToRemove);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);

    void deactivateCurrentShoppingCart(String username);
}
