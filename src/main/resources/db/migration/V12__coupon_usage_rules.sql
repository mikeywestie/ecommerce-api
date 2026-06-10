ALTER TABLE coupons
ADD COLUMN IF NOT EXISTS reusable BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS max_uses_per_customer INTEGER,
ADD COLUMN IF NOT EXISTS max_total_uses INTEGER;

UPDATE coupons
SET reusable = TRUE,
    max_uses_per_customer = NULL,
    max_total_uses = NULL
WHERE code IN ('SAVE10', 'WELCOME250');

CREATE TABLE IF NOT EXISTS coupon_redemptions (
    id BIGSERIAL PRIMARY KEY,
    coupon_id BIGINT NOT NULL REFERENCES coupons(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    order_id BIGINT NOT NULL REFERENCES customer_orders(id),
    redeemed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_coupon_redemptions_coupon_user
ON coupon_redemptions(coupon_id, user_id);

CREATE INDEX IF NOT EXISTS idx_coupon_redemptions_coupon
ON coupon_redemptions(coupon_id);