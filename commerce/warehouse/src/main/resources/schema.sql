CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.warehouse_products (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    width DOUBLE PRECISION NOT NULL,
    height DOUBLE PRECISION NOT NULL,
    depth DOUBLE PRECISION NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (product_id) REFERENCES shopping_store.products(product_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS warehouse.order_bookings (
    order_id UUID NOT NULL PRIMARY KEY,
    delivery_id UUID,
    assembled_at TIMESTAMP,
    shipped_at TIMESTAMP,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ASSEMBLED', 'SHIPPED', 'RETURNED')),
    FOREIGN KEY (order_id) REFERENCES orders.orders(order_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_order_bookings_delivery_id ON warehouse.order_bookings(delivery_id);
CREATE INDEX IF NOT EXISTS idx_order_bookings_status ON warehouse.order_bookings(status);
CREATE INDEX IF NOT EXISTS idx_order_bookings_assembled_at ON warehouse.order_bookings(assembled_at);
CREATE INDEX IF NOT EXISTS idx_order_bookings_shipped_at ON warehouse.order_bookings(shipped_at);