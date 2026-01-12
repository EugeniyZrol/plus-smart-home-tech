CREATE SCHEMA IF NOT EXISTS shopping_store;

CREATE TABLE IF NOT EXISTS shopping_store.products (
    product_id UUID NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255) NOT NULL,
    image_src VARCHAR(255),
    price NUMERIC(38,2) NOT NULL,
    product_category VARCHAR(255) NOT NULL CHECK (product_category IN ('CONTROL','SENSORS','LIGHTING')),
    product_name VARCHAR(255) NOT NULL,
    product_state VARCHAR(255) NOT NULL CHECK (product_state IN ('ACTIVE','DEACTIVATE')),
    quantity_state VARCHAR(255) NOT NULL CHECK (quantity_state IN ('ENDED','FEW','ENOUGH','MANY')),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_products_category ON shopping_store.products(product_category);
CREATE INDEX IF NOT EXISTS idx_products_state ON shopping_store.products(product_state);
CREATE INDEX IF NOT EXISTS idx_products_quantity_state ON shopping_store.products(quantity_state);
CREATE INDEX IF NOT EXISTS idx_products_created_at ON shopping_store.products(created_at);