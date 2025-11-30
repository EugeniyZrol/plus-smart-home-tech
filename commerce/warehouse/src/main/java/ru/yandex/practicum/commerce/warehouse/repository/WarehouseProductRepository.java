package ru.yandex.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, UUID> {

    Optional<WarehouseProduct> findByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    @Query("SELECT wp FROM WarehouseProduct wp WHERE wp.productId IN :productIds")
    List<WarehouseProduct> findByProductIds(@Param("productIds") List<UUID> productIds);
}
