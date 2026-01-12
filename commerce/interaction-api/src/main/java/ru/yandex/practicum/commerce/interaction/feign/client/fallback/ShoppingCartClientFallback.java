package ru.yandex.practicum.commerce.interaction.feign.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.feign.operations.ShoppingCartOperations;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ShoppingCartClientFallback implements ShoppingCartOperations {

    @Override
    public ResponseEntity<ShoppingCartDto> getShoppingCart(String username) {
        log.warn("Сервис корзины недоступен - fallback для получения корзины пользователя: {}", username);
        return ResponseEntity.ok(createEmptyCart());
    }

    @Override
    public ResponseEntity<ShoppingCartDto> getShoppingCartById(UUID shoppingCartId) {
        log.warn("Сервис корзины недоступен - fallback для получения корзины по ID: {}", shoppingCartId);
        ShoppingCartDto cart = createEmptyCart();
        cart.setShoppingCartId(shoppingCartId);
        cart.setUsername("fallback_user_" + shoppingCartId.toString().substring(0, 8));
        return ResponseEntity.ok(cart);
    }

    @Override
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(String username, Map<UUID, Integer> productsToAdd) {
        log.warn("Сервис корзины недоступен - fallback для добавления продуктов");
        return ResponseEntity.ok(createEmptyCart());
    }

    @Override
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(String username, List<UUID> productIdsToRemove) {
        log.warn("Сервис корзины недоступен - fallback для удаления продуктов");
        return ResponseEntity.ok(createEmptyCart());
    }

    @Override
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.warn("Сервис корзины недоступен - fallback для изменения количества");
        return ResponseEntity.ok(createEmptyCart());
    }

    @Override
    public ResponseEntity<Void> deactivateCurrentShoppingCart(String username) {
        log.warn("Сервис корзины недоступен - fallback для деактивации корзины");
        return ResponseEntity.ok().build();
    }

    private ShoppingCartDto createEmptyCart() {
        ShoppingCartDto cart = new ShoppingCartDto();
        cart.setShoppingCartId(UUID.randomUUID());
        cart.setUsername("fallback_user");
        return cart;
    }
}