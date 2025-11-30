package ru.yandex.practicum.commerce.shoppingstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategory(ProductCategory category, Pageable pageable);
}