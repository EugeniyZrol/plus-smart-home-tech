package ru.yandex.practicum.commerce.interaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.ProductDto;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction.enums.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store")
    ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam("category") ProductCategory category,
            Pageable pageable);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ResponseEntity<ProductDto> getProduct(@PathVariable("productId") UUID productId);

    @PutMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> createNewProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    ResponseEntity<Boolean> removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    ResponseEntity<Boolean> setProductQuantityState(
            @RequestParam("productId") UUID productId,
            @RequestParam("quantityState") QuantityState quantityState);
}