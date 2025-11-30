package ru.yandex.practicum.commerce.warehouse.service;

import ru.yandex.practicum.commerce.interaction.dto.*;

import java.util.UUID;

public interface WarehouseService {
    void newProductInWarehouse(NewProductInWarehouseRequest request);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart);

    AddressDto getWarehouseAddress();
}
