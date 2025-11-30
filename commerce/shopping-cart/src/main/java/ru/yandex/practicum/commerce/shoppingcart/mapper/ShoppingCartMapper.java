package ru.yandex.practicum.commerce.shoppingcart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.shoppingcart.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(source = "shoppingCartId", target = "shoppingCartId")
    @Mapping(source = "products", target = "products")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}