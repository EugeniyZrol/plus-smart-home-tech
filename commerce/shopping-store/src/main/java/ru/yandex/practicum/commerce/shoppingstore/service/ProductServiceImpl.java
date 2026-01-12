package ru.yandex.practicum.commerce.shoppingstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.commerce.interaction.dto.shoppingstore.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.shoppingstore.repository.ProductRepository;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction.enums.ProductState;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products;
        if (category == null) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findByProductCategory(category, pageable);
        }

        log.info("Получены продукты, категория: {}, количество: {}", category, products.getTotalElements());
        return products.map(productMapper::toDto);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        ProductDto productDto = productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + productId));
        log.info("Получен продукт: {}", productId);
        return productDto;
    }

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        Product saved = productRepository.save(product);
        log.info("Создан новый продукт: {}", saved.getProductId());
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.getProductId() == null) {
            log.warn("Попытка обновления продукта с пустым ID");
            throw new ProductNotFoundException("ID продукта не может быть пустым для обновления");
        }

        Product existing = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + productDto.getProductId()));

        productMapper.updateEntityFromDto(productDto, existing);
        Product updated = productRepository.save(existing);
        log.info("Обновлен продукт: {}", productDto.getProductId());
        return productMapper.toDto(updated);
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + productId));

        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        log.info("Продукт деактивирован: {}", productId);
        return true;
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + request.getProductId()));

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        log.info("Установлено состояние количества для продукта: {}, состояние: {}",
                request.getProductId(), request.getQuantityState());
        return true;
    }
}