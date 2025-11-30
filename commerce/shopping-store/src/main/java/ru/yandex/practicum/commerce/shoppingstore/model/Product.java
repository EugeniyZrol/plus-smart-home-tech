package ru.yandex.practicum.commerce.shoppingstore.model;

import lombok.Data;
import ru.yandex.practicum.commerce.interaction.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction.enums.ProductState;
import ru.yandex.practicum.commerce.interaction.enums.QuantityState;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private String description;

    private String imageSrc;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false)
    private ProductState productState = ProductState.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}