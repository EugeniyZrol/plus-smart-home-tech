-- Создаём схему если не существует
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'telemetry') THEN
        EXECUTE 'CREATE SCHEMA telemetry';
        RAISE NOTICE 'Схема telemetry создана';
    END IF;
END $$;