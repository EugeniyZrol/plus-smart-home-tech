package ru.yandex.practicum.commerce.shoppingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;

import java.util.UUID;

public interface ProductService {
    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProduct(UUID productId);

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean removeProductFromStore(UUID productId);

    boolean setProductQuantityState(SetProductQuantityStateRequest request);
}
