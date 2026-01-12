package ru.yandex.practicum.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.commerce.interaction.enums.PaymentState;
import ru.yandex.practicum.commerce.interaction.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.interaction.feign.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.interaction.exception.PaymentNotFoundException;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% НДС
    private static final BigDecimal FALLBACK_PRICE = new BigDecimal("1000.00");
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        validateOrderForPayment(orderDto);

        BigDecimal productCost = calculateProductCostBigDecimal(orderDto);
        BigDecimal deliveryCost = orderDto.getDeliveryPrice() != null
                ? orderDto.getDeliveryPrice()
                : BigDecimal.ZERO;
        BigDecimal taxAmount = productCost.multiply(TAX_RATE).setScale(SCALE, ROUNDING_MODE);
        BigDecimal totalCost = productCost.add(deliveryCost).add(taxAmount).setScale(SCALE, ROUNDING_MODE);

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setOrderId(orderDto.getOrderId());
        payment.setState(PaymentState.PENDING);
        payment.setProductTotal(productCost);
        payment.setDeliveryTotal(deliveryCost);
        payment.setFeeTotal(taxAmount);
        payment.setTotalPayment(totalCost);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Создан платеж для заказа {}: {}", orderDto.getOrderId(), savedPayment.getPaymentId());
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        validateOrderForCalculation(orderDto);

        BigDecimal productCost = calculateProductCostBigDecimal(orderDto);
        BigDecimal deliveryCost = orderDto.getDeliveryPrice() != null
                ? orderDto.getDeliveryPrice()
                : BigDecimal.ZERO;
        BigDecimal taxAmount = productCost.multiply(TAX_RATE).setScale(SCALE, ROUNDING_MODE);
        BigDecimal totalCost = productCost.add(deliveryCost).add(taxAmount);

        return totalCost.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    @Transactional
    public void markPaymentAsSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платеж не найден с id: " + paymentId));

        payment.setState(PaymentState.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("Платеж {} отмечен как успешный. Заказ {} ожидает обновления статуса.",
                paymentId, payment.getOrderId());
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        return calculateProductCostBigDecimal(orderDto)
                .setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    @Transactional
    public void markPaymentAsFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платеж не найден с id: " + paymentId));

        payment.setState(PaymentState.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("Платеж {} отмечен как неудачный. Заказ {} ожидает обновления статуса.",
                paymentId, payment.getOrderId());
    }

    private void validateOrderForPayment(OrderDto orderDto) {
        validateOrderForCalculation(orderDto);

        if (orderDto.getOrderId() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не указан идентификатор заказа");
        }
    }

    private void validateOrderForCalculation(OrderDto orderDto) {
        if (orderDto == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Заказ не может быть пустым");
        }

        if (orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров для расчета");
        }
    }

    private BigDecimal calculateProductCostBigDecimal(OrderDto orderDto) {
        validateOrderForCalculation(orderDto);

        if (orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров для расчета стоимости");
        }

        BigDecimal totalProductCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Integer> entry : orderDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            try {
                ProductDto product = shoppingStoreClient.getProduct(productId).getBody();
                if (product != null && product.getPrice() != null) {
                    BigDecimal priceForQuantity = product.getPrice()
                            .multiply(BigDecimal.valueOf(quantity))
                            .setScale(SCALE, ROUNDING_MODE);
                    totalProductCost = totalProductCost.add(priceForQuantity);
                } else {
                    log.warn("Не удалось получить цену для товара: {}", productId);
                    BigDecimal fallbackPriceForQuantity = FALLBACK_PRICE
                            .multiply(BigDecimal.valueOf(quantity))
                            .setScale(SCALE, ROUNDING_MODE);
                    totalProductCost = totalProductCost.add(fallbackPriceForQuantity);
                }
            } catch (Exception e) {
                log.warn("Ошибка при получении цены товара {}: {}", productId, e.getMessage());
                BigDecimal fallbackPriceForQuantity = FALLBACK_PRICE
                        .multiply(BigDecimal.valueOf(quantity))
                        .setScale(SCALE, ROUNDING_MODE);
                totalProductCost = totalProductCost.add(fallbackPriceForQuantity);
            }
        }

        log.debug("Рассчитана стоимость товаров для заказа {}: {}",
                orderDto.getOrderId(), totalProductCost);
        return totalProductCost.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public PaymentDto getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платеж не найден с id: " + paymentId));
        return paymentMapper.toDto(payment);
    }
}