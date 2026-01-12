package ru.yandex.practicum.commerce.interaction.feign.operations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderOperations {

    ResponseEntity<List<OrderDto>> getClientOrders(String username);

    ResponseEntity<OrderDto> createNewOrder(CreateNewOrderRequest request);

    ResponseEntity<OrderDto> productReturn(
            @RequestParam("productReturnRequest") ProductReturnRequest productReturnRequest,
            ProductReturnRequest request);

    ResponseEntity<OrderDto> payment(UUID orderId);

    ResponseEntity<OrderDto> paymentFailed(UUID orderId);

    ResponseEntity<OrderDto> delivery(UUID orderId);

    ResponseEntity<OrderDto> deliveryFailed(UUID orderId);

    ResponseEntity<OrderDto> complete(UUID orderId);

    ResponseEntity<OrderDto> calculateTotalCost(UUID orderId);

    ResponseEntity<OrderDto> calculateDeliveryCost(UUID orderId);

    ResponseEntity<OrderDto> assembly(UUID orderId);

    ResponseEntity<OrderDto> assemblyFailed(UUID orderId);
}