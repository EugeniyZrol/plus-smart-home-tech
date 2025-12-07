package ru.yandex.practicum.commerce.shoppingstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.commerce.interaction.dto.ProductDto;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    void updateEntityFromDto(ProductDto productDto, @MappingTarget Product product);
}