package ru.yandex.practicum.commerce.interaction.feign.operations;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.shoppingcart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartOperations {

    ResponseEntity<ShoppingCartDto> getShoppingCart(String username);

    ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            String username,
            Map<UUID, Integer> productsToAdd);

    ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            String username,
            List<UUID> productIdsToRemove);

    ResponseEntity<ShoppingCartDto> changeProductQuantity(
            String username,
            ChangeProductQuantityRequest request);

    ResponseEntity<Void> deactivateCurrentShoppingCart(String username);

    ResponseEntity<ShoppingCartDto> getShoppingCartById(UUID shoppingCartId);

}