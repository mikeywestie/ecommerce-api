ALTER TABLE products
ADD COLUMN category VARCHAR(100);

ALTER TABLE products
ADD COLUMN image_url VARCHAR(1000);

ALTER TABLE products
ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE products
SET
    category = 'General',
    image_url = 'https://images.unsplash.com/photo-1523275335684-37898b6baf30',
    active = TRUE
WHERE category IS NULL;