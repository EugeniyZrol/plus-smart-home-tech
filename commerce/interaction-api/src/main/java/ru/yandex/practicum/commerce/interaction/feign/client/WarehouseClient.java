package ru.yandex.practicum.commerce.interaction.feign.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.*;
import ru.yandex.practicum.commerce.interaction.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.interaction.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.interaction.feign.client.fallback.WarehouseClientFallback;
import ru.yandex.practicum.commerce.interaction.feign.operations.WarehouseOperations;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", fallback = WarehouseClientFallback.class)
@CircuitBreaker(name = "warehouse")
public interface WarehouseClient extends WarehouseOperations {

    @Override
    @PutMapping("/api/v1/warehouse")
    ResponseEntity<Void> newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @Override
    @PostMapping("/api/v1/warehouse/add")
    ResponseEntity<Void> addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @Override
    @PostMapping("/api/v1/warehouse/check")
    ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto shoppingCart);

    @Override
    @GetMapping("/api/v1/warehouse/address")
    ResponseEntity<AddressDto> getWarehouseAddress();

    @Override
    @PostMapping("/api/v1/warehouse/assembly")
    ResponseEntity<BookedProductsDto> assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request);

    @Override
    @PostMapping("/api/v1/warehouse/shipped")
    ResponseEntity<Void> shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);

    @Override
    @PostMapping("/api/v1/warehouse/return")
    ResponseEntity<Void> acceptReturn(@RequestBody Map<UUID, Integer> products);
}