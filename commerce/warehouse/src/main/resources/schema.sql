-- Создаём схему если не существует
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'warehouse') THEN
        EXECUTE 'CREATE SCHEMA warehouse';
        RAISE NOTICE 'Схема warehouse создана';
    ELSE
        RAISE NOTICE 'Схема warehouse уже существует';
    END IF;
END $$;

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