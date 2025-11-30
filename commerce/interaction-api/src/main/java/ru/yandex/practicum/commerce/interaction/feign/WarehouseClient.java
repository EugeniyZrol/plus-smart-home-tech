package ru.yandex.practicum.commerce.interaction.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.*;

@FeignClient(
        name = "warehouse",
        fallback = WarehouseClientFallback.class
)
@CircuitBreaker(name = "warehouse")
public interface WarehouseClient {

    @PutMapping("/api/v1/warehouse")
    ResponseEntity<Void> newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/add")
    ResponseEntity<Void> addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/check")
    ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto shoppingCart);

    @GetMapping("/api/v1/warehouse/address")
    ResponseEntity<AddressDto> getWarehouseAddress();
}