ALTER TABLE carts
ADD COLUMN coupon_id BIGINT;

ALTER TABLE carts
ADD CONSTRAINT fk_cart_coupon
FOREIGN KEY (coupon_id)
REFERENCES coupons(id);