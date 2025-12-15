package ru.yandex.practicum.commerce.interaction.feign.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.feign.client.fallback.ShoppingCartClientFallback;
import ru.yandex.practicum.commerce.interaction.feign.operations.ShoppingCartOperations;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", fallback = ShoppingCartClientFallback.class)
@CircuitBreaker(name = "shopping-cart")
public interface ShoppingCartClient extends ShoppingCartOperations {

    @Override
    @GetMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam("username") String username);

    @Override
    @PutMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @RequestParam("username") String username,
            @RequestBody Map<UUID, Integer> productsToAdd);

    @Override
    @PostMapping("/api/v1/shopping-cart/remove")
    ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @RequestParam("username") String username,
            @RequestBody List<UUID> productIdsToRemove);

    @Override
    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam("username") String username,
            @RequestBody ChangeProductQuantityRequest request);

    @Override
    @DeleteMapping("/api/v1/shopping-cart")
    ResponseEntity<Void> deactivateCurrentShoppingCart(@RequestParam("username") String username);

    @Override
    @GetMapping("/api/v1/shopping-cart/{shoppingCartId}")
    ResponseEntity<ShoppingCartDto> getShoppingCartById(@PathVariable("shoppingCartId") UUID shoppingCartId);
}