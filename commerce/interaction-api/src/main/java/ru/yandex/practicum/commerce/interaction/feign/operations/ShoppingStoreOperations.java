package ru.yandex.practicum.commerce.interaction.feign.operations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.commerce.interaction.dto.ProductDto;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction.enums.QuantityState;

import java.util.UUID;

public interface ShoppingStoreOperations {

    ResponseEntity<Page<ProductDto>> getProducts(ProductCategory category, Pageable pageable);

    ResponseEntity<ProductDto> getProduct(UUID productId);

    ResponseEntity<ProductDto> createNewProduct(ProductDto productDto);

    ResponseEntity<ProductDto> updateProduct(ProductDto productDto);

    ResponseEntity<Boolean> removeProductFromStore(UUID productId);

    ResponseEntity<Boolean> setProductQuantityState(UUID productId, QuantityState quantityState);
}