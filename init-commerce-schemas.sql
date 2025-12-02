-- Создаем схемы если их нет
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'shopping_store') THEN
        CREATE SCHEMA shopping_store;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'shopping_cart') THEN
        CREATE SCHEMA shopping_cart;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'warehouse') THEN
        CREATE SCHEMA warehouse;
    END IF;
END
$$;

-- Даем права пользователю postgres
GRANT ALL PRIVILEGES ON SCHEMA shopping_store TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA shopping_cart TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA warehouse TO postgres;

-- Устанавливаем search_path для пользователя postgres
ALTER ROLE postgres SET search_path TO public,shopping_store,shopping_cart,warehouse;

-- Даем права на создание таблиц в схемах
GRANT CREATE ON SCHEMA shopping_store TO postgres;
GRANT CREATE ON SCHEMA shopping_cart TO postgres;
GRANT CREATE ON SCHEMA warehouse TO postgres;