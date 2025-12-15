package ru.yandex.practicum.commerce.interaction.dto.shoppingcart;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class ShoppingCartDto {
    private UUID shoppingCartId;
    private String username;
    private Map<UUID, Integer> products;
}