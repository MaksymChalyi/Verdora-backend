-- VERDORA SHOWCASE DATA (synthetic). PostgreSQL / Flyway.
-- Production Flyway migration: applied by default from src/main/resources/db/migration.
-- Do not use real personal information in showcase data.

-- 16 historical customer orders with multiple statuses and realistic purchase dates.
-- Initial total_price is recalculated after order items are inserted below, within the same Flyway transaction.
WITH seed(customer_email, status, placed_at) AS (
    VALUES
    ('olena.kovalenko@verdora.example', 'DELIVERED', '2026-07-04T10:12:00+00:00'::timestamptz),
    ('andrii.melnyk@verdora.example', 'DELIVERED', '2026-07-18T15:38:00+00:00'::timestamptz),
    ('mariia.shevchenko@verdora.example', 'DELIVERED', '2026-08-03T08:48:00+00:00'::timestamptz),
    ('dmytro.bondar@verdora.example', 'CANCELLED', '2026-08-14T17:21:00+00:00'::timestamptz),
    ('kateryna.savchuk@verdora.example', 'DELIVERED', '2026-08-22T12:06:00+00:00'::timestamptz),
    ('roman.petrenko@verdora.example', 'SHIPPED', '2026-09-04T19:50:00+00:00'::timestamptz),
    ('sofia.hrytsenko@verdora.example', 'DELIVERED', '2026-09-13T09:14:00+00:00'::timestamptz),
    ('mykhailo.tkachenko@verdora.example', 'PAID', '2026-09-25T11:26:00+00:00'::timestamptz),
    ('yuliia.kravchuk@verdora.example', 'PAID', '2026-10-02T18:15:00+00:00'::timestamptz),
    ('taras.koval@verdora.example', 'CANCELLED', '2026-10-07T13:40:00+00:00'::timestamptz),
    ('iryna.polishchuk@verdora.example', 'SHIPPED', '2026-10-12T16:35:00+00:00'::timestamptz),
    ('oleksii.levchenko@verdora.example', 'PAID', '2026-10-16T10:44:00+00:00'::timestamptz),
    ('anastasiia.romaniuk@verdora.example', 'PENDING', '2026-10-21T07:54:00+00:00'::timestamptz),
    ('bohdan.moroz@verdora.example', 'SHIPPED', '2026-10-30T18:04:00+00:00'::timestamptz),
    ('olena.kovalenko@verdora.example', 'PAID', '2026-11-02T08:48:00+00:00'::timestamptz),
    ('andrii.melnyk@verdora.example', 'PENDING', '2026-11-06T17:14:00+00:00'::timestamptz)
)
INSERT INTO orders(user_id, status, total_price, created_at)
SELECT u.user_id, s.status, 0.00, s.placed_at
FROM seed s
JOIN users u ON u.email = s.customer_email
WHERE NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.user_id AND o.created_at = s.placed_at
);

-- 20 order lines. Historical purchase prices are snapshots, not current prices.
WITH seed(customer_email, placed_at, product_name, quantity, unit_price) AS (
    VALUES
    ('olena.kovalenko@verdora.example', '2026-07-04T10:12:00+00:00'::timestamptz, 'Apple iPhone 15 128GB, Black', 1, 30999.00),
    ('andrii.melnyk@verdora.example', '2026-07-18T15:38:00+00:00'::timestamptz, 'Sony WH-1000XM5, Black', 1, 12899.00),
    ('andrii.melnyk@verdora.example', '2026-07-18T15:38:00+00:00'::timestamptz, 'Baseus GaN5 Pro Charger 65W, USB-C', 1, 1499.00),
    ('mariia.shevchenko@verdora.example', '2026-08-03T08:48:00+00:00'::timestamptz, 'Lenovo IdeaPad Slim 5 16, Ryzen 7, 16/512GB', 1, 33999.00),
    ('dmytro.bondar@verdora.example', '2026-08-14T17:21:00+00:00'::timestamptz, 'Apple Watch SE GPS 40mm, Midnight', 1, 11999.00),
    ('kateryna.savchuk@verdora.example', '2026-08-22T12:06:00+00:00'::timestamptz, 'TP-Link Tapo P110M, Smart Wi-Fi Plug', 2, 899.00),
    ('kateryna.savchuk@verdora.example', '2026-08-22T12:06:00+00:00'::timestamptz, 'Xiaomi Smart Air Purifier 4 Lite', 1, 7499.00),
    ('roman.petrenko@verdora.example', '2026-09-04T19:50:00+00:00'::timestamptz, 'ASUS Vivobook 15 OLED, Core i5, 16/512GB', 1, 34999.00),
    ('sofia.hrytsenko@verdora.example', '2026-09-13T09:14:00+00:00'::timestamptz, 'JBL Tune 770NC, Black', 1, 4199.00),
    ('mykhailo.tkachenko@verdora.example', '2026-09-25T11:26:00+00:00'::timestamptz, 'Canon EOS R50 RF-S 18-45mm Kit', 1, 33999.00),
    ('yuliia.kravchuk@verdora.example', '2026-10-02T18:15:00+00:00'::timestamptz, 'Logitech MX Master 3S, Graphite', 1, 4299.00),
    ('yuliia.kravchuk@verdora.example', '2026-10-02T18:15:00+00:00'::timestamptz, 'Keychron K8 Pro RGB, Mechanical Keyboard', 1, 5899.00),
    ('taras.koval@verdora.example', '2026-10-07T13:40:00+00:00'::timestamptz, 'Dell UltraSharp U2723QE 27-inch 4K', 1, 23999.00),
    ('iryna.polishchuk@verdora.example', '2026-10-12T16:35:00+00:00'::timestamptz, 'Philips Airfryer HD9252/90, 4.1L', 1, 4199.00),
    ('oleksii.levchenko@verdora.example', '2026-10-16T10:44:00+00:00'::timestamptz, 'Garmin Forerunner 265, Black', 1, 19999.00),
    ('anastasiia.romaniuk@verdora.example', '2026-10-21T07:54:00+00:00'::timestamptz, 'Apple iPad 10.9 Wi-Fi 64GB, Silver', 1, 16999.00),
    ('bohdan.moroz@verdora.example', '2026-10-30T18:04:00+00:00'::timestamptz, 'Samsung Galaxy A55 5G 8/256GB, Awesome Navy', 1, 15999.00),
    ('olena.kovalenko@verdora.example', '2026-11-02T08:48:00+00:00'::timestamptz, 'Anker 737 Power Bank 24000mAh, 140W', 1, 6199.00),
    ('olena.kovalenko@verdora.example', '2026-11-02T08:48:00+00:00'::timestamptz, 'Baseus GaN5 Pro Charger 65W, USB-C', 1, 1499.00),
    ('andrii.melnyk@verdora.example', '2026-11-06T17:14:00+00:00'::timestamptz, 'JBL Charge 5, Portable Bluetooth Speaker', 1, 5999.00)
)
INSERT INTO order_items(order_id, product_id, quantity, price_at_purchase)
SELECT o.order_id, p.product_id, s.quantity, s.unit_price
FROM seed s
JOIN users u ON u.email = s.customer_email
JOIN orders o ON o.user_id = u.user_id AND o.created_at = s.placed_at
JOIN products p ON p.name = s.product_name
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi
    WHERE oi.order_id = o.order_id AND oi.product_id = p.product_id
);

-- Always reconcile totals from actual snapshots; essential for revenue reports.
UPDATE orders o
SET total_price = totals.amount
FROM (
    SELECT oi.order_id, SUM(oi.quantity * oi.price_at_purchase) AS amount
    FROM order_items oi
    JOIN orders source_order ON source_order.order_id = oi.order_id
    JOIN users u ON u.user_id = source_order.user_id
    WHERE u.email LIKE '%@verdora.example'
    GROUP BY oi.order_id
) totals
WHERE o.order_id = totals.order_id;
