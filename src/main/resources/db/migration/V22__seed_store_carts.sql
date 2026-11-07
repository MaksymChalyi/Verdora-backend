-- VERDORA SHOWCASE DATA (synthetic). PostgreSQL / Flyway.
-- Production Flyway migration: applied by default from src/main/resources/db/migration.
-- Do not use real personal information in showcase data.

-- 18 customer carts, including intentionally empty carts.
INSERT INTO carts(user_id, created_at, updated_at)
SELECT u.user_id, u.created_at + INTERVAL '2 days', u.created_at + INTERVAL '4 days'
FROM users u
WHERE u.email LIKE '%@verdora.example'
  AND NOT EXISTS (SELECT 1 FROM carts c WHERE c.user_id = u.user_id);

-- 20 open cart lines, joined by user email and exact product name.
WITH seed(customer_email, product_name, quantity) AS (
    VALUES
    ('olena.kovalenko@verdora.example', 'Logitech MX Master 3S, Graphite', 1),
    ('olena.kovalenko@verdora.example', 'Baseus GaN5 Pro Charger 65W, USB-C', 2),
    ('andrii.melnyk@verdora.example', 'Sony WH-1000XM5, Black', 1),
    ('andrii.melnyk@verdora.example', 'Anker 737 Power Bank 24000mAh, 140W', 1),
    ('mariia.shevchenko@verdora.example', 'Philips Airfryer HD9252/90, 4.1L', 1),
    ('dmytro.bondar@verdora.example', 'Keychron K8 Pro RGB, Mechanical Keyboard', 1),
    ('kateryna.savchuk@verdora.example', 'TP-Link Tapo P110M, Smart Wi-Fi Plug', 2),
    ('kateryna.savchuk@verdora.example', 'Xiaomi Smart Air Purifier 4 Lite', 1),
    ('roman.petrenko@verdora.example', 'Dell UltraSharp U2723QE 27-inch 4K', 1),
    ('sofia.hrytsenko@verdora.example', 'JBL Tune 770NC, Black', 1),
    ('mykhailo.tkachenko@verdora.example', 'Canon EOS R50 RF-S 18-45mm Kit', 1),
    ('yuliia.kravchuk@verdora.example', 'Logitech MX Master 3S, Graphite', 1),
    ('taras.koval@verdora.example', 'IKEA MARKUS, Office Chair, Black', 1),
    ('iryna.polishchuk@verdora.example', 'Philips Airfryer HD9252/90, 4.1L', 1),
    ('oleksii.levchenko@verdora.example', 'Apple Watch SE GPS 40mm, Midnight', 1),
    ('anastasiia.romaniuk@verdora.example', 'JBL Charge 5, Portable Bluetooth Speaker', 1),
    ('bohdan.moroz@verdora.example', 'Samsung Galaxy A55 5G 8/256GB, Awesome Navy', 1),
    ('bohdan.moroz@verdora.example', 'Baseus GaN5 Pro Charger 65W, USB-C', 1),
    ('nataliia.ivanchuk@verdora.example', 'Lenovo IdeaPad Slim 5 16, Ryzen 7, 16/512GB', 1),
    ('vladyslav.kozak@verdora.example', 'JBL Tune 770NC, Black', 2)
)
INSERT INTO cart_items(cart_id, product_id, quantity)
SELECT c.cart_id, p.product_id, s.quantity
FROM seed s
JOIN users u ON u.email = s.customer_email
JOIN carts c ON c.user_id = u.user_id
JOIN products p ON p.name = s.product_name
WHERE NOT EXISTS (
    SELECT 1 FROM cart_items ci
    WHERE ci.cart_id = c.cart_id AND ci.product_id = p.product_id
);
