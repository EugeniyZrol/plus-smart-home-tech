package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(source = "dimension.width", target = "width")
    @Mapping(source = "dimension.height", target = "height")
    @Mapping(source = "dimension.depth", target = "depth")
    WarehouseProduct toEntity(NewProductInWarehouseRequest request);
}