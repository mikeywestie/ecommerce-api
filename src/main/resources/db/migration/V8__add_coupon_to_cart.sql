ALTER TABLE carts
ADD COLUMN IF NOT EXISTS coupon_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_cart_coupon'
    ) THEN
        ALTER TABLE carts
        ADD CONSTRAINT fk_cart_coupon
        FOREIGN KEY (coupon_id)
        REFERENCES coupons(id);
    END IF;
END $$;