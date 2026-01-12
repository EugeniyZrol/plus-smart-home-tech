package ru.yandex.practicum.commerce.interaction.feign.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.interaction.feign.operations.OrderOperations;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class OrderClientFallback implements OrderOperations {

    @Override
    public ResponseEntity<List<OrderDto>> getClientOrders(String username) {
        log.warn("Сервис заказов недоступен - fallback для получения заказов пользователя");
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<OrderDto> createNewOrder(CreateNewOrderRequest request) {
        log.warn("Сервис заказов недоступен - fallback для создания заказа");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> productReturn(
            ProductReturnRequest productReturnRequest,
            ProductReturnRequest request) {

        log.warn("Сервис заказов недоступен - fallback для возврата товаров");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> payment(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для оплаты заказа");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> paymentFailed(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для ошибки оплаты");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> delivery(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для доставки");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> deliveryFailed(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для ошибки доставки");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> complete(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для завершения заказа");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> calculateTotalCost(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для расчета общей стоимости");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> calculateDeliveryCost(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для расчета стоимости доставки");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> assembly(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для сборки заказа");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<OrderDto> assemblyFailed(UUID orderId) {
        log.warn("Сервис заказов недоступен - fallback для ошибки сборки");
        return ResponseEntity.badRequest().build();
    }
}
