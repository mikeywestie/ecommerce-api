ALTER TABLE products
ADD COLUMN IF NOT EXISTS subcategory VARCHAR(100);

ALTER TABLE products
ADD COLUMN IF NOT EXISTS brand VARCHAR(100);

UPDATE products
SET subcategory = 'General'
WHERE subcategory IS NULL;

UPDATE products
SET brand = 'Generic'
WHERE brand IS NULL;