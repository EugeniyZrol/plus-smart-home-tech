package ru.yandex.practicum.commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.dto.*;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interaction.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction.enums.OrderState;
import ru.yandex.practicum.commerce.interaction.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.interaction.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.interaction.feign.client.DeliveryClient;
import ru.yandex.practicum.commerce.interaction.feign.client.PaymentClient;
import ru.yandex.practicum.commerce.interaction.feign.client.ShoppingCartClient;
import ru.yandex.practicum.commerce.interaction.feign.client.WarehouseClient;
import ru.yandex.practicum.commerce.interaction.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.model.OrderItem;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final ShoppingCartClient shoppingCartClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        validateUsername(username);

        List<Order> orders = orderRepository.findByUsername(username);
        log.info("Получены заказы пользователя: {}, количество: {}", username, orders.size());
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        BookedProductsDto bookedProducts = validateProductsInWarehouse(request.getShoppingCart());

        Order order = createOrder(request, bookedProducts);
        Order savedOrder = orderRepository.save(order);

        createDeliveryForOrder(savedOrder, request);

        calculateAndSaveOrderCost(savedOrder);
        orderRepository.save(savedOrder);

        shoppingCartClient.deactivateCurrentShoppingCart(order.getUsername());

        log.info("Создан новый заказ: {} для пользователя: {}", savedOrder.getOrderId(), order.getUsername());
        return orderMapper.toDto(savedOrder);
    }

    private BookedProductsDto validateProductsInWarehouse(ShoppingCartDto shoppingCart) {
        ResponseEntity<BookedProductsDto> warehouseResponse = warehouseClient
                .checkProductQuantityEnoughForShoppingCart(shoppingCart);

        if (!warehouseResponse.getStatusCode().is2xxSuccessful() || warehouseResponse.getBody() == null) {
            throw new NoSpecifiedProductInWarehouseException("Нет заказываемого товара на складе");
        }

        return warehouseResponse.getBody();
    }

    private void createDeliveryForOrder(Order order, CreateNewOrderRequest request) {
        if (order.getDeliveryId() != null) {
            log.info("Доставка уже создана для заказа {}: {}", order.getOrderId(), order.getDeliveryId());
            return;
        }
        try {
            DeliveryDto deliveryDto = getDeliveryDto(request, order);
            ResponseEntity<DeliveryDto> deliveryResponse = deliveryClient.planDelivery(deliveryDto);

            if (deliveryResponse.getStatusCode().is2xxSuccessful() && deliveryResponse.getBody() != null) {
                DeliveryDto plannedDelivery = deliveryResponse.getBody();
                order.setDeliveryId(plannedDelivery.getDeliveryId());
                log.info("Создана доставка для заказа {}: {}", order.getOrderId(), plannedDelivery.getDeliveryId());
            }
        } catch (Exception e) {
            log.error("Не удалось создать доставку для заказа {}: {}", order.getOrderId(), e.getMessage());
        }
    }

    private DeliveryDto getDeliveryDto(CreateNewOrderRequest request, Order savedOrder) {
        try {
            if (request == null || savedOrder == null || request.getDeliveryAddress() == null) {
                throw new IllegalArgumentException("Неверные параметры для создания доставки");
            }

            ResponseEntity<AddressDto> warehouseAddressResponse = warehouseClient.getWarehouseAddress();

            if (!warehouseAddressResponse.getStatusCode().is2xxSuccessful() || warehouseAddressResponse.getBody() == null) {
                throw new RuntimeException("Не удалось получить адрес склада");
            }

            AddressDto warehouseAddress = warehouseAddressResponse.getBody();

            DeliveryDto deliveryDto = new DeliveryDto();
            deliveryDto.setOrderId(savedOrder.getOrderId());
            deliveryDto.setFromAddress(warehouseAddress);

            AddressDto toAddress = new AddressDto();
            toAddress.setCountry(request.getDeliveryAddress().getCountry());
            toAddress.setCity(request.getDeliveryAddress().getCity());
            toAddress.setStreet(request.getDeliveryAddress().getStreet());
            toAddress.setHouse(request.getDeliveryAddress().getHouse());
            toAddress.setFlat(request.getDeliveryAddress().getFlat());
            deliveryDto.setToAddress(toAddress);

            return deliveryDto;
        } catch (Exception e) {
            log.error("Ошибка при создании данных для доставки: {}", e.getMessage());
            throw new RuntimeException("Не удалось подготовить данные для доставки: " + e.getMessage(), e);
        }
    }

    private Order createOrder(CreateNewOrderRequest request, BookedProductsDto bookedProducts) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setUsername(getUsernameFromShoppingCart(request.getShoppingCart().getShoppingCartId()));
        order.setState(OrderState.ON_PAYMENT);

        order.setCountry(request.getDeliveryAddress().getCountry());
        order.setCity(request.getDeliveryAddress().getCity());
        order.setStreet(request.getDeliveryAddress().getStreet());
        order.setHouse(request.getDeliveryAddress().getHouse());
        order.setFlat(request.getDeliveryAddress().getFlat());

        order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        order.setFragile(bookedProducts.getFragile());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        request.getShoppingCart().getProducts().forEach((productId, quantity) -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(productId);
            item.setQuantity(quantity);
            order.getItems().add(item);
        });

        return order;
    }

    private void calculateAndSaveOrderCost(Order order) {
        try {
            OrderDto calculatedOrder = calculateTotalCost(order.getOrderId());
            order.setTotalPrice(calculatedOrder.getTotalPrice());
            order.setProductPrice(calculatedOrder.getProductPrice());
            order.setDeliveryPrice(calculatedOrder.getDeliveryPrice());
            log.info("Стоимость заказа {} рассчитана: {}", order.getOrderId(), calculatedOrder.getTotalPrice());
        } catch (Exception e) {
            log.warn("Не удалось рассчитать стоимость заказа {}: {}", order.getOrderId(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        order.setState(OrderState.PRODUCT_RETURNED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Заказ {} возвращён", order.getOrderId());
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        try {
            OrderDto orderDto = orderMapper.toDto(order);

            ResponseEntity<PaymentDto> paymentResponse = paymentClient.payment(orderDto);

            if (paymentResponse.getStatusCode().is2xxSuccessful() && paymentResponse.getBody() != null) {
                PaymentDto paymentDto = paymentResponse.getBody();
                order.setPaymentId(paymentDto.getPaymentId());
                order.setState(OrderState.PAID);
                log.info("Платеж создан для заказа {}: {}", orderId, paymentDto.getPaymentId());
            } else {
                order.setState(OrderState.PAYMENT_FAILED);
                log.error("Не удалось создать платеж для заказа {}", orderId);
            }
        } catch (Exception e) {
            order.setState(OrderState.PAYMENT_FAILED);
            log.error("Ошибка при создании платежа для заказа {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Не удалось создать платеж: " + e.getMessage(), e);
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        log.info("Заказ {} оплачен", orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        order.setState(OrderState.PAYMENT_FAILED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Оплата заказа {} не удалась", orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        order.setState(OrderState.DELIVERED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Заказ {} доставлен", orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        order.setState(OrderState.DELIVERY_FAILED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Доставка заказа {} не удалась", orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        order.setState(OrderState.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Заказ {} завершён", orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        try {
            OrderDto orderDto = orderMapper.toDto(order);

            ResponseEntity<BigDecimal> totalCostResponse = paymentClient.getTotalCost(orderDto);

            if (totalCostResponse.getStatusCode().is2xxSuccessful() && totalCostResponse.getBody() != null) {
                BigDecimal totalCost = totalCostResponse.getBody();

                BigDecimal productCost = calculateProductPrice(order);

                BigDecimal deliveryPrice = calculateDeliveryPrice(order);

                order.setProductPrice(productCost);
                order.setDeliveryPrice(deliveryPrice);
                order.setTotalPrice(totalCost);

                log.info("Рассчитана общая стоимость заказа {} через payment сервис: {}", orderId, totalCost);
            } else {
                BigDecimal productPrice = calculateProductPrice(order);
                BigDecimal deliveryPrice = calculateDeliveryPrice(order);
                BigDecimal totalPrice = productPrice.add(deliveryPrice);

                order.setProductPrice(productPrice);
                order.setDeliveryPrice(deliveryPrice);
                order.setTotalPrice(totalPrice);

                log.warn("Используется fallback расчет стоимости для заказа {}: {}", orderId, totalPrice);
            }
        } catch (Exception e) {
            BigDecimal productPrice = calculateProductPrice(order);
            BigDecimal deliveryPrice = calculateDeliveryPrice(order);
            BigDecimal totalPrice = productPrice.add(deliveryPrice);

            order.setProductPrice(productPrice);
            order.setDeliveryPrice(deliveryPrice);
            order.setTotalPrice(totalPrice);

            log.warn("Ошибка при расчете стоимости через payment сервис для заказа {}: {}. Используется fallback: {}",
                    orderId, e.getMessage(), totalPrice);
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        BigDecimal deliveryPrice = calculateDeliveryPrice(order);
        order.setDeliveryPrice(deliveryPrice);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Рассчитана стоимость доставки заказа {}: {}", orderId, deliveryPrice);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        try {
            AssemblyProductsForOrderRequest assemblyRequest = new AssemblyProductsForOrderRequest();
            assemblyRequest.setOrderId(orderId);

            Map<UUID, Integer> products = order.getItems().stream()
                    .collect(Collectors.toMap(
                            OrderItem::getProductId,
                            OrderItem::getQuantity
                    ));
            assemblyRequest.setProducts(products);

            ResponseEntity<BookedProductsDto> assemblyResponse = warehouseClient.assemblyProductsForOrder(assemblyRequest);

            if (assemblyResponse.getStatusCode().is2xxSuccessful()) {
                order.setState(OrderState.ASSEMBLED);
                log.info("Заказ {} собран на складе", orderId);
            } else {
                order.setState(OrderState.ASSEMBLY_FAILED);
                log.error("Не удалось собрать заказ {} на складе", orderId);
            }
        } catch (Exception e) {
            order.setState(OrderState.ASSEMBLY_FAILED);
            log.error("Ошибка при сборке заказа {} на складе: {}", orderId, e.getMessage());
            throw new RuntimeException("Не удалось собрать заказ на складе: " + e.getMessage(), e);
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ"));

        order.setState(OrderState.ASSEMBLY_FAILED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Сборка заказа {} не удалась", orderId);
        return orderMapper.toDto(savedOrder);
    }

    private String getUsernameFromShoppingCart(UUID shoppingCartId) {
        try {
            ResponseEntity<ShoppingCartDto> cartResponse = shoppingCartClient.getShoppingCartById(shoppingCartId);
            if (cartResponse.getStatusCode().is2xxSuccessful() && cartResponse.getBody() != null) {
                String username = cartResponse.getBody().getUsername();
                if (username != null && !username.trim().isEmpty()) {
                    log.info("Получен username из корзины {}: {}", shoppingCartId, username);
                    return username;
                }
            }
        } catch (Exception e) {
            log.warn("Не удалось получить информацию о корзине {}: {}", shoppingCartId, e.getMessage());
        }

        String fallbackUsername = "user_" + shoppingCartId.toString().substring(0, 8);
        log.warn("Используем fallback username для корзины {}: {}", shoppingCartId, fallbackUsername);
        return fallbackUsername;
    }

    private BigDecimal calculateProductPrice(Order order) {
        try {
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderId(order.getOrderId());
            orderDto.setProducts(order.getItems().stream()
                    .collect(Collectors.toMap(
                            OrderItem::getProductId,
                            OrderItem::getQuantity
                    )));

            ResponseEntity<BigDecimal> productCostResponse = paymentClient.productCost(orderDto);

            if (productCostResponse.getStatusCode().is2xxSuccessful() && productCostResponse.getBody() != null) {
                BigDecimal productCost = productCostResponse.getBody();
                log.debug("Стоимость товаров рассчитана через payment сервис для заказа {}: {}",
                        order.getOrderId(), productCost);
                return productCost;
            } else {
                log.warn("Payment сервис не вернул стоимость товаров для заказа {}", order.getOrderId());
                return calculateFallbackProductPrice(order);
            }
        } catch (Exception e) {
            log.warn("Ошибка при расчете стоимости товаров через payment сервис для заказа {}: {}",
                    order.getOrderId(), e.getMessage());
            return calculateFallbackProductPrice(order);
        }
    }

    private BigDecimal calculateFallbackProductPrice(Order order) {
        int totalItems = order.getItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        return BigDecimal.valueOf(totalItems).multiply(BigDecimal.valueOf(1000.0));
    }

    private BigDecimal calculateDeliveryPrice(Order order) {
        try {
            OrderDto orderDto = orderMapper.toDto(order);

            BigDecimal deliveryCost = deliveryClient.deliveryCost(orderDto).getBody();
            return deliveryCost != null ? deliveryCost : calculateFallbackDeliveryPrice(order);
        } catch (Exception e) {
            log.warn("Не удалось рассчитать стоимость доставки через delivery сервис: {}", e.getMessage());
            return calculateFallbackDeliveryPrice(order);
        }
    }

    private BigDecimal calculateFallbackDeliveryPrice(Order order) {
        BigDecimal basePrice = BigDecimal.valueOf(300.0);

        BigDecimal weightFactor = BigDecimal.ZERO;
        if (order.getDeliveryWeight() != null && order.getDeliveryWeight() > 0 && order.getDeliveryWeight() <= 1000) {
            weightFactor = BigDecimal.valueOf(order.getDeliveryWeight()).multiply(BigDecimal.valueOf(10));
        }

        BigDecimal volumeFactor = BigDecimal.ZERO;
        if (order.getDeliveryVolume() != null && order.getDeliveryVolume() > 0 && order.getDeliveryVolume() <= 100) {
            volumeFactor = BigDecimal.valueOf(order.getDeliveryVolume()).multiply(BigDecimal.valueOf(5));
        }

        BigDecimal fragileFactor = Boolean.TRUE.equals(order.getFragile()) ?
                BigDecimal.valueOf(200.0) :
                BigDecimal.ZERO;

        BigDecimal result = basePrice.add(weightFactor).add(volumeFactor).add(fragileFactor);

        BigDecimal maxFallbackPrice = BigDecimal.valueOf(5000.0);
        if (result.compareTo(maxFallbackPrice) > 0) {
            log.warn("Fallback стоимость доставки слишком высокая: {} для заказа {}. Ограничиваем до {}",
                    result, order.getOrderId(), maxFallbackPrice);
            return maxFallbackPrice;
        }

        return result;
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("Попытка доступа с пустым именем пользователя");
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}