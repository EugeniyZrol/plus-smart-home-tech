CREATE SCHEMA IF NOT EXISTS shopping_cart;

CREATE TABLE IF NOT EXISTS shopping_cart.shopping_carts (
    shopping_cart_id UUID NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(255) NOT NULL CHECK (status IN ('ACTIVE','DEACTIVATED')),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart.shopping_cart_items (
    shopping_cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    PRIMARY KEY (shopping_cart_id, product_id),
    FOREIGN KEY (shopping_cart_id) REFERENCES shopping_cart.shopping_carts(shopping_cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES shopping_store.products(product_id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_shopping_carts_username ON shopping_cart.shopping_carts(username);
CREATE INDEX IF NOT EXISTS idx_shopping_carts_status ON shopping_cart.shopping_carts(status);
CREATE INDEX IF NOT EXISTS idx_shopping_carts_created_at ON shopping_cart.shopping_carts(created_at);