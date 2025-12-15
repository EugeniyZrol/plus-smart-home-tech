package ru.yandex.practicum.commerce.interaction.feign.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction.enums.ProductState;
import ru.yandex.practicum.commerce.interaction.enums.QuantityState;
import ru.yandex.practicum.commerce.interaction.feign.operations.ShoppingStoreOperations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
public class ShoppingStoreClientFallback implements ShoppingStoreOperations {

    @Override
    public ResponseEntity<Page<ProductDto>> getProducts(ProductCategory category, Pageable pageable) {
        log.warn("Сервис магазина недоступен - fallback для получения товаров по категории: {}", category);
        Page<ProductDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        return ResponseEntity.ok(emptyPage);
    }

    @Override
    public ResponseEntity<ProductDto> getProduct(UUID productId) {
        log.warn("Сервис магазина недоступен - fallback для получения товара: {}", productId);

        ProductDto fallbackProduct = new ProductDto();
        fallbackProduct.setProductId(productId);
        fallbackProduct.setProductName("Товар временно недоступен");
        fallbackProduct.setDescription("Информация о товаре временно недоступна");
        fallbackProduct.setImageSrc("");
        fallbackProduct.setPrice(BigDecimal.ZERO);
        fallbackProduct.setProductCategory(ProductCategory.CONTROL);
        fallbackProduct.setQuantityState(QuantityState.ENDED);
        fallbackProduct.setProductState(ProductState.ACTIVE);

        return ResponseEntity.ok(fallbackProduct);
    }

    @Override
    public ResponseEntity<ProductDto> createNewProduct(ProductDto productDto) {
        log.warn("Сервис магазина недоступен - fallback для создания товара");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<ProductDto> updateProduct(ProductDto productDto) {
        log.warn("Сервис магазина недоступен - fallback для обновления товара");
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Boolean> removeProductFromStore(UUID productId) {
        log.warn("Сервис магазина недоступен - fallback для удаления товара");
        return ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<Boolean> setProductQuantityState(UUID productId, QuantityState quantityState) {
        log.warn("Сервис магазина недоступен - fallback для установки статуса количества");
        return ResponseEntity.ok(false);
    }
}