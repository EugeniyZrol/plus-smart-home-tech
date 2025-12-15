CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE IF NOT EXISTS orders.orders (
    order_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    shopping_cart_id UUID NOT NULL,
    payment_id UUID,
    delivery_id UUID,
    state VARCHAR(50) NOT NULL,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price NUMERIC(10,2),
    delivery_price NUMERIC(10,2),
    product_price NUMERIC(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    country VARCHAR(100),
    city VARCHAR(100),
    street VARCHAR(255),
    house VARCHAR(50),
    flat VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS orders.order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (order_id) REFERENCES orders.orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES shopping_store.products(product_id) ON DELETE RESTRICT,
    CONSTRAINT unique_order_product UNIQUE (order_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_orders_username ON orders.orders(username);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders.orders(created_at);
CREATE INDEX IF NOT EXISTS idx_orders_state ON orders.orders(state);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON orders.order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON orders.order_items(product_id);