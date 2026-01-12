package ru.yandex.practicum.commerce.interaction.feign.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.interaction.feign.client.fallback.OrderClientFallback;
import ru.yandex.practicum.commerce.interaction.feign.operations.OrderOperations;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", fallback = OrderClientFallback.class)
@CircuitBreaker(name = "order")
public interface OrderClient extends OrderOperations {

    @Override
    @GetMapping("/api/v1/order")
    ResponseEntity<List<OrderDto>> getClientOrders(@RequestParam("username") String username);

    @Override
    @PutMapping("/api/v1/order")
    ResponseEntity<OrderDto> createNewOrder(@RequestBody CreateNewOrderRequest request);

    @Override
    @PostMapping("/api/v1/order/return")
    ResponseEntity<OrderDto> productReturn(
            @RequestParam("productReturnRequest") ProductReturnRequest productReturnRequest,
            @RequestBody ProductReturnRequest request);

    @Override
    @PostMapping("/api/v1/order/payment")
    ResponseEntity<OrderDto> payment(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/order/payment/failed")
    ResponseEntity<OrderDto> paymentFailed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/order/delivery")
    ResponseEntity<OrderDto> delivery(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/order/delivery/failed")
    ResponseEntity<OrderDto> deliveryFailed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/order/completed")
    ResponseEntity<OrderDto> complete(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/order/calculate/total")
    ResponseEntity<OrderDto> calculateTotalCost(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/order/calculate/delivery")
    ResponseEntity<OrderDto> calculateDeliveryCost(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/order/assembly")
    ResponseEntity<OrderDto> assembly(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/order/assembly/failed")
    ResponseEntity<OrderDto> assemblyFailed(@RequestBody UUID orderId);
}