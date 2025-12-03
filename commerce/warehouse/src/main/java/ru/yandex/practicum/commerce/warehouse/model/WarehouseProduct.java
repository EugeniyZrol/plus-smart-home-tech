package ru.yandex.practicum.commerce.warehouse.model;

import lombok.Data;

import jakarta.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "warehouse_products")
public class WarehouseProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(nullable = false)
    private Double width;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double depth;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Boolean fragile = false;

    public Double getVolume() {
        return width * height * depth;
    }
}
