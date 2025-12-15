package ru.yandex.practicum.commerce.interaction.feign.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction.enums.QuantityState;
import ru.yandex.practicum.commerce.interaction.feign.client.fallback.ShoppingStoreClientFallback;
import ru.yandex.practicum.commerce.interaction.feign.operations.ShoppingStoreOperations;

import java.util.UUID;

@FeignClient(name = "shopping-store", fallback = ShoppingStoreClientFallback.class)
@CircuitBreaker(name = "shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreOperations {

    @Override
    @GetMapping("/api/v1/shopping-store")
    ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam("category") ProductCategory category,
            Pageable pageable);

    @Override
    @GetMapping("/api/v1/shopping-store/{productId}")
    ResponseEntity<ProductDto> getProduct(@PathVariable("productId") UUID productId);

    @Override
    @PutMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> createNewProduct(@RequestBody ProductDto productDto);

    @Override
    @PostMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto);

    @Override
    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    ResponseEntity<Boolean> removeProductFromStore(@RequestBody UUID productId);

    @Override
    @PostMapping("/api/v1/shopping-store/quantityState")
    ResponseEntity<Boolean> setProductQuantityState(
            @RequestParam("productId") UUID productId,
            @RequestParam("quantityState") QuantityState quantityState);
}