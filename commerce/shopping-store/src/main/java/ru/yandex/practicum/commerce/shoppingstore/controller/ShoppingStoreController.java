package ru.yandex.practicum.commerce.shoppingstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.interaction.enums.QuantityState;
import ru.yandex.practicum.commerce.interaction.feign.operations.ShoppingStoreOperations;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreOperations {

    private final ProductService productService;

    @Override
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam ProductCategory category,
            Pageable pageable) {
        Page<ProductDto> products = productService.getProducts(category, pageable);
        return ResponseEntity.ok(products);
    }

    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID productId) {
        ProductDto product = productService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @Override
    @PutMapping
    public ResponseEntity<ProductDto> createNewProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto created = productService.createNewProduct(productDto);
        return ResponseEntity.ok(created);
    }

    @Override
    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto updated = productService.updateProduct(productDto);
        return ResponseEntity.ok(updated);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Boolean> removeProductFromStore(@RequestBody UUID productId) {
        boolean result = productService.removeProductFromStore(productId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam QuantityState quantityState) {

        SetProductQuantityStateRequest request = new SetProductQuantityStateRequest();
        request.setProductId(productId);
        request.setQuantityState(quantityState);

        boolean result = productService.setProductQuantityState(request);
        return ResponseEntity.ok(result);
    }
}