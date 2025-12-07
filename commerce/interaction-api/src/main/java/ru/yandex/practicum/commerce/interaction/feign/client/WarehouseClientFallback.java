package ru.yandex.practicum.commerce.interaction.feign.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interaction.dto.*;
import ru.yandex.practicum.commerce.interaction.feign.operations.WarehouseOperations;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseOperations {

    @Override
    public ResponseEntity<Void> newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.warn("Сервис склада недоступен - fallback для newProductInWarehouse");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.warn("Сервис склада недоступен - fallback для addProductToWarehouse");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        log.warn("Сервис склада недоступен - fallback для проверки доступности");

        BookedProductsDto fallbackResponse = new BookedProductsDto();
        fallbackResponse.setDeliveryWeight(0.0);
        fallbackResponse.setDeliveryVolume(0.0);
        fallbackResponse.setFragile(false);

        return ResponseEntity.ok(fallbackResponse);
    }

    @Override
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.warn("Сервис склада недоступен - fallback для получения адреса");

        AddressDto fallbackAddress = new AddressDto();
        fallbackAddress.setCountry("Россия");
        fallbackAddress.setCity("Москва");
        fallbackAddress.setStreet("Основная улица");
        fallbackAddress.setHouse("1");

        return ResponseEntity.ok(fallbackAddress);
    }
}