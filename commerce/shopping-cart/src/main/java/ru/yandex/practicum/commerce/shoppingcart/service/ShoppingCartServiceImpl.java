package ru.yandex.practicum.commerce.shoppingcart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.dto.warehouse.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.interaction.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.interaction.feign.client.WarehouseClient;
import ru.yandex.practicum.commerce.shoppingcart.model.ShoppingCart;
import ru.yandex.practicum.commerce.shoppingcart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.interaction.enums.ShoppingCartStatus;
import ru.yandex.practicum.commerce.shoppingcart.mapper.ShoppingCartMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsernameAndStatus(username, ShoppingCartStatus.ACTIVE)
                .orElseGet(() -> createNewShoppingCart(username));

        log.info("Получена корзина для пользователя: {}", username);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> productsToAdd) {
        validateUsername(username);

        ShoppingCartDto checkCart = new ShoppingCartDto();
        checkCart.setProducts(productsToAdd);

        warehouseClient.checkProductQuantityEnoughForShoppingCart(checkCart);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsernameAndStatus(username, ShoppingCartStatus.ACTIVE)
                .orElseGet(() -> createNewShoppingCart(username));

        for (Map.Entry<UUID, Integer> entry : productsToAdd.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantityToAdd = entry.getValue();

            Integer currentQuantity = shoppingCart.getProducts().getOrDefault(productId, 0);
            shoppingCart.getProducts().put(productId, currentQuantity + quantityToAdd);
        }

        ShoppingCart saved = shoppingCartRepository.save(shoppingCart);
        log.info("Добавлены продукты в корзину пользователя: {}, продукты: {}", username, productsToAdd);
        return shoppingCartMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIdsToRemove) {
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsernameAndStatus(username, ShoppingCartStatus.ACTIVE)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Не найдена активная корзина для пользователя"));

        Set<UUID> cartProductIds = shoppingCart.getProducts().keySet();
        List<UUID> productsToRemove = productIdsToRemove.stream()
                .filter(cartProductIds::contains)
                .collect(Collectors.toList());

        Set<UUID> missingProducts = productIdsToRemove.stream()
                .filter(productId -> !cartProductIds.contains(productId))
                .collect(Collectors.toSet());

        if (!missingProducts.isEmpty()) {
            log.warn("Попытка удалить несуществующие продукты из корзины: {}, пользователь: {}",
                    missingProducts, username);
        }

        productsToRemove.forEach(shoppingCart.getProducts()::remove);

        ShoppingCart saved = shoppingCartRepository.save(shoppingCart);
        log.info("Удалены продукты из корзины пользователя: {}, продукты: {}", username, productsToRemove);
        return shoppingCartMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsernameAndStatus(username, ShoppingCartStatus.ACTIVE)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Не найдена активная корзина для пользователя"));

        UUID productId = request.getProductId();
        Integer newQuantity = request.getNewQuantity();

        if (!shoppingCart.getProducts().containsKey(productId)) {
            log.warn("Попытка изменить количество несуществующего продукта: {}, пользователь: {}", productId, username);
            throw new NoProductsInShoppingCartException("Продукт не найден в корзине: " + productId);
        }

        shoppingCart.getProducts().put(productId, newQuantity);

        ShoppingCart saved = shoppingCartRepository.save(shoppingCart);
        log.info("Изменено количество продукта в корзине: пользователь: {}, продукт: {}, новое количество: {}",
                username, productId, newQuantity);
        return shoppingCartMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsernameAndStatus(username, ShoppingCartStatus.ACTIVE)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Не найдена активная корзина для пользователя"));

        shoppingCart.setStatus(ShoppingCartStatus.DEACTIVATED);
        shoppingCartRepository.save(shoppingCart);
        log.info("Деактивирована корзина пользователя: {}", username);
    }

    @Override
    public ShoppingCartDto getShoppingCartById(UUID shoppingCartId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина не найдена: " + shoppingCartId));

        log.info("Получена корзина по ID: {}", shoppingCartId);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    private ShoppingCart createNewShoppingCart(String username) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUsername(username);
        newCart.setProducts(new HashMap<>());
        newCart.setStatus(ShoppingCartStatus.ACTIVE);
        ShoppingCart saved = shoppingCartRepository.save(newCart);
        log.info("Создана новая корзина для пользователя: {}", username);
        return saved;
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("Попытка доступа с пустым именем пользователя");
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}