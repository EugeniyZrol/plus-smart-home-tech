-- Создаём схему если не существует
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'shopping_store') THEN
        EXECUTE 'CREATE SCHEMA shopping_store';
        RAISE NOTICE 'Схема shopping_store создана';
    ELSE
        RAISE NOTICE 'Схема shopping_store уже существует';
    END IF;
END $$;

-- Создаём таблицы
CREATE TABLE IF NOT EXISTS shopping_store.products (
    product_id uuid not null,
    created_at timestamp(6),
    description varchar(255) not null,
    image_src varchar(255),
    price numeric(38,2) not null,
    product_category varchar(255) not null check (product_category in ('CONTROL','SENSORS','LIGHTING')),
    product_name varchar(255) not null,
    product_state varchar(255) not null check (product_state in ('ACTIVE','DEACTIVATE')),
    quantity_state varchar(255) not null check (quantity_state in ('ENDED','FEW','ENOUGH','MANY')),
    updated_at timestamp(6),
    primary key (product_id)
);