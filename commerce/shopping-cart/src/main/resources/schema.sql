-- Простое создание схемы
CREATE SCHEMA IF NOT EXISTS shopping_cart;

-- Создаём таблицы
CREATE TABLE IF NOT EXISTS shopping_cart.shopping_carts (
    shopping_cart_id uuid not null,
    created_at timestamp(6),
    status varchar(255) not null check (status in ('ACTIVE','DEACTIVATED')),
    updated_at timestamp(6),
    username varchar(255) not null,
    primary key (shopping_cart_id)
);

CREATE TABLE IF NOT EXISTS shopping_cart.shopping_cart_items (
    shopping_cart_id uuid not null,
    quantity integer,
    product_id uuid not null,
    primary key (shopping_cart_id, product_id)
);