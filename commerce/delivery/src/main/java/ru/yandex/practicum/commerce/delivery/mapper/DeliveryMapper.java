package ru.yandex.practicum.commerce.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction.dto.AddressDto;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.delivery.model.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "deliveryId", source = "deliveryId")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "deliveryState", source = "deliveryState")
    @Mapping(target = "deliveryCost", source = "deliveryCost")
    @Mapping(target = "estimatedDeliveryTime", source = "estimatedDeliveryTime")
    @Mapping(target = "fromAddress", expression = "java(mapToAddressDtoFrom(delivery))")
    @Mapping(target = "toAddress", expression = "java(mapToAddressDtoTo(delivery))")
    DeliveryDto toDto(Delivery delivery);

    @Mapping(target = "deliveryId", source = "deliveryId")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "deliveryState", source = "deliveryState")
    @Mapping(target = "deliveryCost", source = "deliveryCost")
    @Mapping(target = "estimatedDeliveryTime", source = "estimatedDeliveryTime")
    @Mapping(target = "fromCountry", expression = "java(mapFromAddressCountry(deliveryDto))")
    @Mapping(target = "fromCity", expression = "java(mapFromAddressCity(deliveryDto))")
    @Mapping(target = "fromStreet", expression = "java(mapFromAddressStreet(deliveryDto))")
    @Mapping(target = "fromHouse", expression = "java(mapFromAddressHouse(deliveryDto))")
    @Mapping(target = "fromFlat", expression = "java(mapFromAddressFlat(deliveryDto))")
    @Mapping(target = "toCountry", expression = "java(mapToAddressCountry(deliveryDto))")
    @Mapping(target = "toCity", expression = "java(mapToAddressCity(deliveryDto))")
    @Mapping(target = "toStreet", expression = "java(mapToAddressStreet(deliveryDto))")
    @Mapping(target = "toHouse", expression = "java(mapToAddressHouse(deliveryDto))")
    @Mapping(target = "toFlat", expression = "java(mapToAddressFlat(deliveryDto))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Delivery toEntity(DeliveryDto deliveryDto);

    default AddressDto mapToAddressDtoFrom(Delivery delivery) {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(delivery.getFromCountry());
        addressDto.setCity(delivery.getFromCity());
        addressDto.setStreet(delivery.getFromStreet());
        addressDto.setHouse(delivery.getFromHouse());
        addressDto.setFlat(delivery.getFromFlat());
        return addressDto;
    }

    default AddressDto mapToAddressDtoTo(Delivery delivery) {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(delivery.getToCountry());
        addressDto.setCity(delivery.getToCity());
        addressDto.setStreet(delivery.getToStreet());
        addressDto.setHouse(delivery.getToHouse());
        addressDto.setFlat(delivery.getToFlat());
        return addressDto;
    }

    default String mapFromAddressCountry(DeliveryDto dto) {
        return dto.getFromAddress() != null ? dto.getFromAddress().getCountry() : null;
    }

    default String mapFromAddressCity(DeliveryDto dto) {
        return dto.getFromAddress() != null ? dto.getFromAddress().getCity() : null;
    }

    default String mapFromAddressStreet(DeliveryDto dto) {
        return dto.getFromAddress() != null ? dto.getFromAddress().getStreet() : null;
    }

    default String mapFromAddressHouse(DeliveryDto dto) {
        return dto.getFromAddress() != null ? dto.getFromAddress().getHouse() : null;
    }

    default String mapFromAddressFlat(DeliveryDto dto) {
        return dto.getFromAddress() != null ? dto.getFromAddress().getFlat() : null;
    }

    default String mapToAddressCountry(DeliveryDto dto) {
        return dto.getToAddress() != null ? dto.getToAddress().getCountry() : null;
    }

    default String mapToAddressCity(DeliveryDto dto) {
        return dto.getToAddress() != null ? dto.getToAddress().getCity() : null;
    }

    default String mapToAddressStreet(DeliveryDto dto) {
        return dto.getToAddress() != null ? dto.getToAddress().getStreet() : null;
    }

    default String mapToAddressHouse(DeliveryDto dto) {
        return dto.getToAddress() != null ? dto.getToAddress().getHouse() : null;
    }

    default String mapToAddressFlat(DeliveryDto dto) {
        return dto.getToAddress() != null ? dto.getToAddress().getFlat() : null;
    }
}