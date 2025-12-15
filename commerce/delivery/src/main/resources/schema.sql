CREATE SCHEMA IF NOT EXISTS delivery;

CREATE TABLE IF NOT EXISTS delivery.deliveries (
    delivery_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_state VARCHAR(50) NOT NULL,
    from_country VARCHAR(100),
    from_city VARCHAR(100),
    from_street VARCHAR(255),
    from_house VARCHAR(50),
    from_flat VARCHAR(50),
    to_country VARCHAR(100),
    to_city VARCHAR(100),
    to_street VARCHAR(255),
    to_house VARCHAR(50),
    to_flat VARCHAR(50),
    delivery_cost NUMERIC(10,2),
    estimated_delivery_time INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_deliveries_order_id ON delivery.deliveries(order_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_state ON delivery.deliveries(delivery_state);