package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.dto.*;
import ru.yandex.practicum.commerce.interaction.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.interaction.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.interaction.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseProductRepository warehouseProductRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS;

    static {
        SecureRandom random = new SecureRandom();
        CURRENT_ADDRESS = ADDRESSES[random.nextInt(ADDRESSES.length)];
        log.info("Адрес склада инициализирован: {}", CURRENT_ADDRESS);
    }

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseProductRepository.existsByProductId(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Продукт уже существует на складе: " + request.getProductId());
        }

        WarehouseProduct warehouseProduct = warehouseMapper.toEntity(request);
        warehouseProduct.setQuantity(0);

        warehouseProductRepository.save(warehouseProduct);
        log.info("Новый продукт добавлен на склад: {}", request.getProductId());
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct warehouseProduct = warehouseProductRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Продукт не найден на складе: " + request.getProductId()));

        warehouseProduct.setQuantity(warehouseProduct.getQuantity() + request.getQuantity());
        warehouseProductRepository.save(warehouseProduct);

        log.info("Добавлено {} единиц продукта {} на склад. Новое количество: {}",
                request.getQuantity(), request.getProductId(), warehouseProduct.getQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        Map<UUID, Integer> cartProducts = shoppingCart.getProducts();

        if (cartProducts.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Корзина покупок пуста");
        }

        List<UUID> productIds = new ArrayList<>(cartProducts.keySet());
        List<WarehouseProduct> warehouseProducts = warehouseProductRepository.findByProductIds(productIds);

        Map<UUID, WarehouseProduct> warehouseProductMap = warehouseProducts.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, wp -> wp));

        List<String> insufficientProducts = new ArrayList<>();
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Integer> cartEntry : cartProducts.entrySet()) {
            UUID productId = cartEntry.getKey();
            Integer requestedQuantity = cartEntry.getValue();

            WarehouseProduct warehouseProduct = warehouseProductMap.get(productId);

            if (warehouseProduct == null) {
                insufficientProducts.add(productId + " (не найден на складе)");
                continue;
            }

            if (warehouseProduct.getQuantity() < requestedQuantity) {
                insufficientProducts.add(String.format("%s (запрошено: %d, доступно: %d)",
                        productId, requestedQuantity, warehouseProduct.getQuantity()));
                continue;
            }

            totalWeight += warehouseProduct.getWeight() * requestedQuantity;
            totalVolume += warehouseProduct.getVolume() * requestedQuantity;
            if (warehouseProduct.getFragile()) {
                hasFragile = true;
            }
        }

        if (!insufficientProducts.isEmpty()) {
            String errorMessage = "Недостаточное количество для продуктов: " + String.join(", ", insufficientProducts);
            throw new ProductInShoppingCartLowQuantityInWarehouse(errorMessage);
        }

        BookedProductsDto bookedProducts = new BookedProductsDto();
        bookedProducts.setDeliveryWeight(totalWeight);
        bookedProducts.setDeliveryVolume(totalVolume);
        bookedProducts.setFragile(hasFragile);

        log.info("Продукты успешно проверены для корзины {}. Вес: {}, Объем: {}, Хрупкий: {}",
                shoppingCart.getShoppingCartId(), totalWeight, totalVolume, hasFragile);

        return bookedProducts;
    }

    @Override
    public AddressDto getWarehouseAddress() {
        AddressDto address = new AddressDto();
        address.setCountry(CURRENT_ADDRESS);
        address.setCity(CURRENT_ADDRESS);
        address.setStreet(CURRENT_ADDRESS);
        address.setHouse(CURRENT_ADDRESS);
        address.setFlat(CURRENT_ADDRESS);

        log.debug("Возвращаем адрес склада: {}", CURRENT_ADDRESS);
        return address;
    }
}