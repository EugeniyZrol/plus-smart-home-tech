CREATE SCHEMA IF NOT EXISTS shopping_store;
CREATE SCHEMA IF NOT EXISTS shopping_cart;
CREATE SCHEMA IF NOT EXISTS warehouse;
GRANT ALL ON SCHEMA shopping_store TO postgres;
GRANT ALL ON SCHEMA shopping_cart TO postgres;
GRANT ALL ON SCHEMA warehouse TO postgres;