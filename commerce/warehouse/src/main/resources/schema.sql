-- Простое создание схемы
CREATE SCHEMA IF NOT EXISTS warehouse;

-- Создаём таблицы
CREATE TABLE IF NOT EXISTS warehouse.warehouse_products (
    id uuid not null,
    depth float(53) not null,
    fragile boolean not null,
    height float(53) not null,
    product_id uuid not null,
    quantity integer not null,
    weight float(53) not null,
    width float(53) not null,
    primary key (id)
);