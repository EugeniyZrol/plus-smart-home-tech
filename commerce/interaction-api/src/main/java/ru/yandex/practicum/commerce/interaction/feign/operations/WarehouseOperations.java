package ru.yandex.practicum.commerce.interaction.feign.operations;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.commerce.interaction.dto.*;

public interface WarehouseOperations {

    ResponseEntity<Void> newProductInWarehouse(NewProductInWarehouseRequest request);

    ResponseEntity<Void> addProductToWarehouse(AddProductToWarehouseRequest request);

    ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart);

    ResponseEntity<AddressDto> getWarehouseAddress();
}