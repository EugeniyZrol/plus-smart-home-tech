package ru.yandex.practicum.commerce.shoppingcart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.feign.operations.ShoppingCartOperations;
import ru.yandex.practicum.commerce.shoppingcart.service.ShoppingCartService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService shoppingCartService;

    @Override
    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam String username) {
        ShoppingCartDto shoppingCart = shoppingCartService.getShoppingCart(username);
        return ResponseEntity.ok(shoppingCart);
    }

    @Override
    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @RequestParam String username,
            @RequestBody Map<UUID, Integer> productsToAdd) {
        ShoppingCartDto shoppingCart = shoppingCartService.addProductToShoppingCart(username, productsToAdd);
        return ResponseEntity.ok(shoppingCart);
    }

    @Override
    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @RequestParam String username,
            @RequestBody List<UUID> productIdsToRemove) {
        ShoppingCartDto shoppingCart = shoppingCartService.removeFromShoppingCart(username, productIdsToRemove);
        return ResponseEntity.ok(shoppingCart);
    }

    @Override
    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam String username,
            @Valid @RequestBody ChangeProductQuantityRequest request) {
        ShoppingCartDto shoppingCart = shoppingCartService.changeProductQuantity(username, request);
        return ResponseEntity.ok(shoppingCart);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentShoppingCart(@RequestParam String username) {
        shoppingCartService.deactivateCurrentShoppingCart(username);
        return ResponseEntity.ok().build();
    }
}