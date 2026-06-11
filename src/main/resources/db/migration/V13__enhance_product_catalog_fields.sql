ALTER TABLE products
ADD COLUMN IF NOT EXISTS subcategory VARCHAR(100),
ADD COLUMN IF NOT EXISTS brand VARCHAR(100);

UPDATE products
SET
    subcategory = 'General',
    brand = 'Generic'
WHERE subcategory IS NULL
    OR brand IS NULL;