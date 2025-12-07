-- 1. Создаём базы данных
SELECT 'CREATE DATABASE telemetry'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'telemetry')\gexec

SELECT 'CREATE DATABASE commerce'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'commerce')\gexec

-- 2. Инициализируем базу telemetry
\c telemetry
CREATE SCHEMA IF NOT EXISTS telemetry;
GRANT ALL ON SCHEMA telemetry TO postgres;

-- Таблицы telemetry
CREATE TABLE IF NOT EXISTS telemetry.scenarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hub_id VARCHAR,
    name VARCHAR,
    UNIQUE(hub_id, name)
);

CREATE TABLE IF NOT EXISTS telemetry.sensors (
    id VARCHAR PRIMARY KEY,
    hub_id VARCHAR
);

CREATE TABLE IF NOT EXISTS telemetry.conditions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR,
    operation VARCHAR,
    value INTEGER
);

CREATE TABLE IF NOT EXISTS telemetry.actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR,
    value INTEGER
);

CREATE TABLE IF NOT EXISTS telemetry.scenario_conditions (
    scenario_id BIGINT REFERENCES telemetry.scenarios(id),
    sensor_id VARCHAR REFERENCES telemetry.sensors(id),
    condition_id BIGINT REFERENCES telemetry.conditions(id),
    PRIMARY KEY (scenario_id, sensor_id, condition_id)
);

CREATE TABLE IF NOT EXISTS telemetry.scenario_actions (
    scenario_id BIGINT REFERENCES telemetry.scenarios(id),
    sensor_id VARCHAR REFERENCES telemetry.sensors(id),
    action_id BIGINT REFERENCES telemetry.actions(id),
    PRIMARY KEY (scenario_id, sensor_id, action_id)
);

-- 3. Инициализируем базу commerce
\c commerce
CREATE SCHEMA IF NOT EXISTS shopping_store;
CREATE SCHEMA IF NOT EXISTS shopping_cart;
CREATE SCHEMA IF NOT EXISTS warehouse;

GRANT ALL ON SCHEMA shopping_store TO postgres;
GRANT ALL ON SCHEMA shopping_cart TO postgres;
GRANT ALL ON SCHEMA warehouse TO postgres;