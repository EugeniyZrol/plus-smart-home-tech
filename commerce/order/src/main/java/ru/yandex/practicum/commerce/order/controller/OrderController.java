package ru.yandex.practicum.commerce.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.interaction.feign.operations.OrderOperations;
import ru.yandex.practicum.commerce.order.service.OrderService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderOperations {

    private final OrderService orderService;

    @Override
    @GetMapping
    public ResponseEntity<List<OrderDto>> getClientOrders(@RequestParam String username) {
        List<OrderDto> orders = orderService.getClientOrders(username);
        return ResponseEntity.ok(orders);
    }

    @Override
    @PutMapping
    public ResponseEntity<OrderDto> createNewOrder(@Valid @RequestBody CreateNewOrderRequest request) {
        OrderDto order = orderService.createNewOrder(request);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/return")
    public ResponseEntity<OrderDto> productReturn(
            @RequestParam("productReturnRequest") ProductReturnRequest productReturnRequest,
            @Valid @RequestBody ProductReturnRequest request) {

        OrderDto order = orderService.productReturn(request);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/payment")
    public ResponseEntity<OrderDto> payment(@RequestBody UUID orderId) {
        OrderDto order = orderService.payment(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/payment/failed")
    public ResponseEntity<OrderDto> paymentFailed(@RequestBody UUID orderId) {
        OrderDto order = orderService.paymentFailed(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/delivery")
    public ResponseEntity<OrderDto> delivery(@RequestBody UUID orderId) {
        OrderDto order = orderService.delivery(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/delivery/failed")
    public ResponseEntity<OrderDto> deliveryFailed(@RequestBody UUID orderId) {
        OrderDto order = orderService.deliveryFailed(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/completed")
    public ResponseEntity<OrderDto> complete(@RequestBody UUID orderId) {
        OrderDto order = orderService.complete(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/calculate/total")
    public ResponseEntity<OrderDto> calculateTotalCost(@RequestBody UUID orderId) {
        OrderDto order = orderService.calculateTotalCost(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public ResponseEntity<OrderDto> calculateDeliveryCost(@RequestBody UUID orderId) {
        OrderDto order = orderService.calculateDeliveryCost(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/assembly")
    public ResponseEntity<OrderDto> assembly(@RequestBody UUID orderId) {
        OrderDto order = orderService.assembly(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    @PostMapping("/assembly/failed")
    public ResponseEntity<OrderDto> assemblyFailed(@RequestBody UUID orderId) {
        OrderDto order = orderService.assemblyFailed(orderId);
        return ResponseEntity.ok(order);
    }
}