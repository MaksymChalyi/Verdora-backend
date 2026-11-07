-- VERDORA SHOWCASE DATA (synthetic). PostgreSQL / Flyway.
-- Production Flyway migration: applied by default from src/main/resources/db/migration.
-- Do not use real personal information in showcase data.

-- 20 favorite-product relationships, synthetic customer behavior.
WITH seed(customer_email, product_name, saved_at) AS (
    VALUES
    ('olena.kovalenko@verdora.example', 'Apple iPhone 15 128GB, Black', '2026-09-01T00:00:00+00:00'::timestamptz),
    ('olena.kovalenko@verdora.example', 'Apple Watch SE GPS 40mm, Midnight', '2026-09-03T01:00:00+00:00'::timestamptz),
    ('andrii.melnyk@verdora.example', 'ASUS Vivobook 15 OLED, Core i5, 16/512GB', '2026-09-05T02:00:00+00:00'::timestamptz),
    ('andrii.melnyk@verdora.example', 'Sony WH-1000XM5, Black', '2026-09-07T03:00:00+00:00'::timestamptz),
    ('mariia.shevchenko@verdora.example', 'Apple iPad 10.9 Wi-Fi 64GB, Silver', '2026-09-09T04:00:00+00:00'::timestamptz),
    ('mariia.shevchenko@verdora.example', 'Philips Airfryer HD9252/90, 4.1L', '2026-09-11T05:00:00+00:00'::timestamptz),
    ('dmytro.bondar@verdora.example', 'Logitech MX Master 3S, Graphite', '2026-09-13T06:00:00+00:00'::timestamptz),
    ('kateryna.savchuk@verdora.example', 'TP-Link Tapo P110M, Smart Wi-Fi Plug', '2026-09-15T07:00:00+00:00'::timestamptz),
    ('kateryna.savchuk@verdora.example', 'Xiaomi Smart Air Purifier 4 Lite', '2026-09-17T00:00:00+00:00'::timestamptz),
    ('roman.petrenko@verdora.example', 'Dell UltraSharp U2723QE 27-inch 4K', '2026-09-19T01:00:00+00:00'::timestamptz),
    ('sofia.hrytsenko@verdora.example', 'JBL Tune 770NC, Black', '2026-09-21T02:00:00+00:00'::timestamptz),
    ('mykhailo.tkachenko@verdora.example', 'Canon EOS R50 RF-S 18-45mm Kit', '2026-09-23T03:00:00+00:00'::timestamptz),
    ('yuliia.kravchuk@verdora.example', 'Logitech MX Master 3S, Graphite', '2026-09-25T04:00:00+00:00'::timestamptz),
    ('yuliia.kravchuk@verdora.example', 'Keychron K8 Pro RGB, Mechanical Keyboard', '2026-09-27T05:00:00+00:00'::timestamptz),
    ('taras.koval@verdora.example', 'IKEA MARKUS, Office Chair, Black', '2026-09-29T06:00:00+00:00'::timestamptz),
    ('iryna.polishchuk@verdora.example', 'Philips Airfryer HD9252/90, 4.1L', '2026-10-01T07:00:00+00:00'::timestamptz),
    ('oleksii.levchenko@verdora.example', 'Garmin Forerunner 265, Black', '2026-10-03T00:00:00+00:00'::timestamptz),
    ('anastasiia.romaniuk@verdora.example', 'Apple iPad 10.9 Wi-Fi 64GB, Silver', '2026-10-05T01:00:00+00:00'::timestamptz),
    ('bohdan.moroz@verdora.example', 'Samsung Galaxy A55 5G 8/256GB, Awesome Navy', '2026-10-07T02:00:00+00:00'::timestamptz),
    ('nataliia.ivanchuk@verdora.example', 'Anker 737 Power Bank 24000mAh, 140W', '2026-10-09T03:00:00+00:00'::timestamptz)
)
INSERT INTO favorites(user_id, product_id, created_at)
SELECT u.user_id, p.product_id, s.saved_at
FROM seed s
JOIN users u ON u.email = s.customer_email
JOIN products p ON p.name = s.product_name
WHERE NOT EXISTS (
    SELECT 1 FROM favorites f
    WHERE f.user_id = u.user_id AND f.product_id = p.product_id
);
