package ru.yandex.practicum.commerce.interaction.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interaction.dto.*;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClient {

    @Override
    public ResponseEntity<Void> newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.warn("Warehouse service unavailable - using fallback for newProductInWarehouse");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.warn("Warehouse service unavailable - using fallback for addProductToWarehouse");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        log.warn("Warehouse service unavailable - using fallback for availability check");

        BookedProductsDto fallbackResponse = new BookedProductsDto();
        fallbackResponse.setDeliveryWeight(0.0);
        fallbackResponse.setDeliveryVolume(0.0);
        fallbackResponse.setFragile(false);

        return ResponseEntity.ok(fallbackResponse);
    }

    @Override
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.warn("Warehouse service unavailable - using fallback for address");

        AddressDto fallbackAddress = new AddressDto();
        fallbackAddress.setCountry("Russia");
        fallbackAddress.setCity("Moscow");
        fallbackAddress.setStreet("Default Street");
        fallbackAddress.setHouse("1");

        return ResponseEntity.ok(fallbackAddress);
    }
}