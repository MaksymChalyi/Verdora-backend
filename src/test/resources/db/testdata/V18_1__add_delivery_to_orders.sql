-- Доставка замовлення: спосіб, адреса або точка самовивозу, вартість доставки.
-- Копія для тестів: src/test/resources/db/testdata/V18_1__add_delivery_to_orders.sql (змінювати разом).
-- delivery_method = NULL лише для замовлень, створених до появи доставки.
ALTER TABLE orders ADD COLUMN delivery_method VARCHAR(32);
ALTER TABLE orders ADD COLUMN delivery_address VARCHAR(512);
ALTER TABLE orders ADD COLUMN pickup_point_id BIGINT;
ALTER TABLE orders ADD COLUMN shipping_cost DECIMAL(10, 2) NOT NULL DEFAULT 0;
