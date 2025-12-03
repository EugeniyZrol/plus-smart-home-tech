-- 1. Создаём схему telemetry
CREATE SCHEMA IF NOT EXISTS telemetry;
GRANT ALL ON SCHEMA telemetry TO postgres;

-- 2. Создаём коммерс-схемы
CREATE SCHEMA IF NOT EXISTS shopping_store;
CREATE SCHEMA IF NOT EXISTS shopping_cart;
CREATE SCHEMA IF NOT EXISTS warehouse;

GRANT ALL ON SCHEMA shopping_store TO postgres;
GRANT ALL ON SCHEMA shopping_cart TO postgres;
GRANT ALL ON SCHEMA warehouse TO postgres;

-- 3. Создаём таблицы telemetry в схеме telemetry
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

-- 4. Функции и триггеры
CREATE OR REPLACE FUNCTION telemetry.check_hub_id()
RETURNS TRIGGER AS
$$
BEGIN
    IF (SELECT hub_id FROM telemetry.scenarios WHERE id = NEW.scenario_id) !=
       (SELECT hub_id FROM telemetry.sensors WHERE id = NEW.sensor_id) THEN
        RAISE EXCEPTION 'Hub IDs do not match for scenario_id % and sensor_id %', NEW.scenario_id, NEW.sensor_id;
    END IF;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_bi_scenario_conditions_hub_id_check
BEFORE INSERT ON telemetry.scenario_conditions
FOR EACH ROW
EXECUTE FUNCTION telemetry.check_hub_id();

CREATE OR REPLACE TRIGGER tr_bi_scenario_actions_hub_id_check
BEFORE INSERT ON telemetry.scenario_actions
FOR EACH ROW
EXECUTE FUNCTION telemetry.check_hub_id();