CREATE KEYSPACE IF NOT EXISTS catalog_ks
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS catalog_ks.products (
    id UUID PRIMARY KEY,
    name TEXT,
    category TEXT,
    price DECIMAL,
    in_stock BOOLEAN
);

CREATE INDEX IF NOT EXISTS product_category_idx ON catalog_ks.products(category);
CREATE INDEX IF NOT EXISTS product_stock_idx ON catalog_ks.products(in_stock);