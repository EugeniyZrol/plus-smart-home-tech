package ru.yandex.practicum.commerce.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.payment.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "productTotal", source = "productTotal")
    @Mapping(target = "deliveryTotal", source = "deliveryTotal")
    @Mapping(target = "feeTotal", source = "feeTotal")
    @Mapping(target = "totalPayment", source = "totalPayment")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "productTotal", source = "productTotal")
    @Mapping(target = "deliveryTotal", source = "deliveryTotal")
    @Mapping(target = "feeTotal", source = "feeTotal")
    @Mapping(target = "totalPayment", source = "totalPayment")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    Payment toEntity(PaymentDto dto);
}