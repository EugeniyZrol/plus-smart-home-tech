CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE IF NOT EXISTS payment.payments (
    payment_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    state VARCHAR(50) NOT NULL,
    product_total NUMERIC(10,2) NOT NULL,
    delivery_total NUMERIC(10,2),
    fee_total NUMERIC(10,2),
    total_payment NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders.orders(order_id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payment.payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_state ON payment.payments(state);
CREATE INDEX IF NOT EXISTS idx_payments_created_at ON payment.payments(created_at);