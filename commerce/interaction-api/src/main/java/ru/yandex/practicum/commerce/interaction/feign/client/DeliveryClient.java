package ru.yandex.practicum.commerce.interaction.feign.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.feign.client.fallback.DeliveryClientFallback;
import ru.yandex.practicum.commerce.interaction.feign.operations.DeliveryOperations;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(
        name = "delivery",
        fallback = DeliveryClientFallback.class
)
@CircuitBreaker(name = "delivery")
public interface DeliveryClient extends DeliveryOperations {

    @Override
    @PutMapping("/api/v1/delivery")
    ResponseEntity<DeliveryDto> planDelivery(@RequestBody DeliveryDto deliveryDto);

    @Override
    @PostMapping("/api/v1/delivery/successful")
    ResponseEntity<Void> deliverySuccessful(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/delivery/picked")
    ResponseEntity<Void> deliveryPicked(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/delivery/failed")
    ResponseEntity<Void> deliveryFailed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/api/v1/delivery/cost")
    ResponseEntity<BigDecimal> deliveryCost(@RequestBody OrderDto orderDto);

    @Override
    @GetMapping("/api/v1/delivery/{deliveryId}")
    ResponseEntity<DeliveryDto> getDelivery(@PathVariable("deliveryId") UUID deliveryId);
}