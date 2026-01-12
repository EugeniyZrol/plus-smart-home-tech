package ru.yandex.practicum.commerce.interaction.feign.operations;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.commerce.interaction.dto.*;
import ru.yandex.practicum.commerce.interaction.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.interaction.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.NewProductInWarehouseRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseOperations {

    ResponseEntity<Void> newProductInWarehouse(NewProductInWarehouseRequest request);

    ResponseEntity<Void> addProductToWarehouse(AddProductToWarehouseRequest request);

    ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart);

    ResponseEntity<AddressDto> getWarehouseAddress();

    ResponseEntity<BookedProductsDto> assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    ResponseEntity<Void> shippedToDelivery(ShippedToDeliveryRequest request);

    ResponseEntity<Void> acceptReturn(Map<UUID, Integer> products);
}