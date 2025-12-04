-- Создаём схему если не существует
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'shopping_cart') THEN
        EXECUTE 'CREATE SCHEMA shopping_cart';
        RAISE NOTICE 'Схема shopping_cart создана';
    ELSE
        RAISE NOTICE 'Схема shopping_cart уже существует';
    END IF;
END $$;

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