ALTER TABLE customer_orders
ADD COLUMN coupon_code VARCHAR(100);

ALTER TABLE customer_orders
ADD COLUMN discount_amount NUMERIC(12, 2) NOT NULL DEFAULT 0;