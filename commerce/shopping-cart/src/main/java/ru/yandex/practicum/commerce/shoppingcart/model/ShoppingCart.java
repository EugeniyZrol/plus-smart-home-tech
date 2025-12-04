package ru.yandex.practicum.commerce.shoppingcart.model;

import lombok.Data;
import ru.yandex.practicum.commerce.interaction.enums.ShoppingCartStatus;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(schema = "shopping_cart", name = "shopping_carts")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID shoppingCartId;

    @Column(nullable = false)
    private String username;

    @ElementCollection
    @CollectionTable(
            schema = "shopping_cart",
            name = "shopping_cart_items",
            joinColumns = @JoinColumn(name = "shopping_cart_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShoppingCartStatus status = ShoppingCartStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}