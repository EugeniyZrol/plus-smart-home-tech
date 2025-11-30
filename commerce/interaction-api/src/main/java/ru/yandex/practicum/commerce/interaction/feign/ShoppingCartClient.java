package ru.yandex.practicum.commerce.interaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam("username") String username);

    @PutMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @RequestParam("username") String username,
            @RequestBody Map<UUID, Integer> productsToAdd);

    @PostMapping("/api/v1/shopping-cart/remove")
    ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @RequestParam("username") String username,
            @RequestBody List<UUID> productIdsToRemove);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam("username") String username,
            @RequestBody ChangeProductQuantityRequest request);

    @DeleteMapping("/api/v1/shopping-cart")
    ResponseEntity<Void> deactivateCurrentShoppingCart(@RequestParam("username") String username);
}