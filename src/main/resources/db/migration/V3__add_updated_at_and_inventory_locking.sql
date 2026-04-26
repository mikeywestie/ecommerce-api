ALTER TABLE products
ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE;

UPDATE products
SET updated_at = created_at
WHERE updated_at IS NULL;

ALTER TABLE products
ALTER COLUMN updated_at SET NOT NULL;


ALTER TABLE inventory
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;