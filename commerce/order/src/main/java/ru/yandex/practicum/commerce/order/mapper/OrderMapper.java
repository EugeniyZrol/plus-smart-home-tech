package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction.dto.AddressDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.model.OrderItem;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "shoppingCartId", source = "shoppingCartId")
    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "deliveryId", source = "deliveryId")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "deliveryWeight", source = "deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "deliveryVolume")
    @Mapping(target = "fragile", source = "fragile")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "deliveryPrice", source = "deliveryPrice")
    @Mapping(target = "productPrice", source = "productPrice")
    @Mapping(target = "deliveryAddress", expression = "java(mapToAddressDto(order))")
    @Mapping(target = "products", expression = "java(mapItemsToProducts(order))")
    OrderDto toDto(Order order);

    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "shoppingCartId", source = "shoppingCartId")
    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "deliveryId", source = "deliveryId")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "deliveryWeight", source = "deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "deliveryVolume")
    @Mapping(target = "fragile", source = "fragile")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "deliveryPrice", source = "deliveryPrice")
    @Mapping(target = "productPrice", source = "productPrice")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "street", ignore = true)
    @Mapping(target = "house", ignore = true)
    @Mapping(target = "flat", ignore = true)
    Order toEntity(OrderDto dto);

    default Map<UUID, Integer> mapItemsToProducts(Order order) {
        if (order.getItems() == null) {
            return Map.of();
        }
        return order.getItems().stream()
                .collect(Collectors.toMap(
                        OrderItem::getProductId,
                        OrderItem::getQuantity
                ));
    }

    default AddressDto mapToAddressDto(Order order) {
        if (order.getCountry() == null &&
                order.getCity() == null &&
                order.getStreet() == null) {
            return null;
        }

        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(order.getCountry());
        addressDto.setCity(order.getCity());
        addressDto.setStreet(order.getStreet());
        addressDto.setHouse(order.getHouse());
        addressDto.setFlat(order.getFlat());
        return addressDto;
    }
}