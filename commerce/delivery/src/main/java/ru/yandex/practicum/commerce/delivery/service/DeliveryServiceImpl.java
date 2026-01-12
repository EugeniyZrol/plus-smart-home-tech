package ru.yandex.practicum.commerce.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.interaction.enums.DeliveryState;
import ru.yandex.practicum.commerce.interaction.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.interaction.feign.client.WarehouseClient;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final WarehouseClient warehouseClient;

    private static final BigDecimal BASE_COST = new BigDecimal("5.00");
    private static final BigDecimal FRAGILE_MULTIPLIER = new BigDecimal("0.20");
    private static final BigDecimal WEIGHT_MULTIPLIER = new BigDecimal("0.30");
    private static final BigDecimal VOLUME_MULTIPLIER = new BigDecimal("0.20");
    private static final BigDecimal ADDRESS_DIFFERENT_MULTIPLIER = new BigDecimal("0.20");

    @Override
    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setDeliveryId(UUID.randomUUID());
        delivery.setDeliveryState(DeliveryState.CREATED);
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setUpdatedAt(LocalDateTime.now());

        if (deliveryDto.getDeliveryCost() != null) {
            delivery.setDeliveryCost(deliveryDto.getDeliveryCost());
        } else {
            delivery.setDeliveryCost(BigDecimal.ZERO);
        }

        Delivery savedDelivery = deliveryRepository.save(delivery);

        log.info("Создана доставка для заказа {}: {}", deliveryDto.getOrderId(), savedDelivery.getDeliveryId());
        return deliveryMapper.toDto(savedDelivery);
    }

    @Override
    @Transactional
    public void markDeliveryAsSuccessful(UUID orderId) {
        Delivery delivery = findDeliveryByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        delivery.setUpdatedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        log.info("Доставка для заказа {} отмечена как успешная. Заказ ожидает обновления статуса.", orderId);
    }

    @Override
    @Transactional
    public void markDeliveryAsPicked(UUID orderId) {
        Delivery delivery = findDeliveryByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        delivery.setUpdatedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        log.info("Доставка для заказа {} отмечена как полученная курьером. Заказ ожидает обновления статуса.", orderId);

        try {
            ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
            request.setOrderId(orderId);
            request.setDeliveryId(delivery.getDeliveryId());

            warehouseClient.shippedToDelivery(request);
            log.info("Товары для заказа {} переданы в доставку", orderId);
        } catch (Exception e) {
            log.warn("Не удалось уведомить склад о передаче товаров: {}", orderId, e);
        }
    }

    @Override
    @Transactional
    public void markDeliveryAsFailed(UUID orderId) {
        Delivery delivery = findDeliveryByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.FAILED);
        delivery.setUpdatedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        log.info("Доставка для заказа {} отмечена как неудачная. Заказ ожидает обновления статуса.", orderId);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        if (orderDto == null || orderDto.getOrderId() == null) {
            throw new NoDeliveryFoundException("Заказ не найден для расчета стоимости доставки");
        }

        Delivery delivery = findDeliveryByOrderId(orderDto.getOrderId());

        BigDecimal cost = calculateCostInternal(delivery, orderDto);

        delivery.setDeliveryCost(cost);
        delivery.setUpdatedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        log.info("Рассчитана стоимость доставки для заказа {}: {}", orderDto.getOrderId(), cost);
        return cost;
    }

    private BigDecimal calculateCostInternal(Delivery delivery, OrderDto orderDto) {
        BigDecimal cost = BASE_COST;

        String fromStreet = delivery.getFromStreet();
        if (fromStreet != null) {
            if (fromStreet.contains("ADDRESS_2")) {
                BigDecimal addressMultiplierCost = BASE_COST.multiply(new BigDecimal("2.0"));
                cost = cost.add(addressMultiplierCost);
            }
        }

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            BigDecimal fragileAddition = cost.multiply(FRAGILE_MULTIPLIER);
            cost = cost.add(fragileAddition);
        }

        if (orderDto.getDeliveryWeight() != null) {
            BigDecimal weightCost = BigDecimal.valueOf(orderDto.getDeliveryWeight())
                    .multiply(WEIGHT_MULTIPLIER);
            cost = cost.add(weightCost);
        }

        if (orderDto.getDeliveryVolume() != null) {
            BigDecimal volumeCost = BigDecimal.valueOf(orderDto.getDeliveryVolume())
                    .multiply(VOLUME_MULTIPLIER);
            cost = cost.add(volumeCost);
        }

        if (delivery.getFromStreet() != null && delivery.getToStreet() != null) {
            if (!delivery.getFromStreet().equals(delivery.getToStreet())) {
                BigDecimal addressAddition = cost.multiply(ADDRESS_DIFFERENT_MULTIPLIER);
                cost = cost.add(addressAddition);
            }
        }

        return cost.setScale(2, RoundingMode.HALF_UP);
    }

    private Delivery findDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException(
                        "Не найдена доставка для заказа с id: " + orderId));
    }

    @Override
    public DeliveryDto getDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена с id: " + deliveryId));
        return deliveryMapper.toDto(delivery);
    }
}