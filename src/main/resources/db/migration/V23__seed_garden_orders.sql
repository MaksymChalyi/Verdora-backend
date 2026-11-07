-- VERDORA GARDEN SHOWCASE DATA | synthetic records for a public portfolio deployment.
-- Product prices are illustrative UAH values; customer contact details are fictitious.
-- PostgreSQL / Flyway; migration runs exactly once on a fresh database.

-- 50 historical orders (varied statuses) with 50 order items.
-- Exactly one product line per order, quantity varies; totals equal quantity * historical unit price.
-- This preserves the requested count of exactly 50 records per table.
WITH seed(email, created_at, status, total_price) AS (
    VALUES
    ('client001@verdora.example', '2026-07-10T10:00:00+00:00', 'DELIVERED', '799.00'),
    ('client002@verdora.example', '2026-07-15T11:00:00+00:00', 'DELIVERED', '1398.00'),
    ('client003@verdora.example', '2026-07-20T12:00:00+00:00', 'DELIVERED', '2097.00'),
    ('client004@verdora.example', '2026-07-25T13:00:00+00:00', 'DELIVERED', '2796.00'),
    ('client005@verdora.example', '2026-07-30T14:00:00+00:00', 'DELIVERED', '699.00'),
    ('client006@verdora.example', '2026-08-04T15:00:00+00:00', 'DELIVERED', '1598.00'),
    ('client007@verdora.example', '2026-08-09T16:00:00+00:00', 'DELIVERED', '2097.00'),
    ('client008@verdora.example', '2026-08-14T10:00:00+00:00', 'DELIVERED', '2796.00'),
    ('client009@verdora.example', '2026-08-19T11:00:00+00:00', 'DELIVERED', '419.00'),
    ('client010@verdora.example', '2026-08-24T12:00:00+00:00', 'DELIVERED', '838.00'),
    ('client011@verdora.example', '2026-08-29T13:00:00+00:00', 'DELIVERED', '1395.00'),
    ('client012@verdora.example', '2026-09-03T14:00:00+00:00', 'DELIVERED', '1676.00'),
    ('client013@verdora.example', '2026-09-08T15:00:00+00:00', 'DELIVERED', '419.00'),
    ('client014@verdora.example', '2026-09-13T16:00:00+00:00', 'DELIVERED', '138.00'),
    ('client015@verdora.example', '2026-09-18T10:00:00+00:00', 'DELIVERED', '207.00'),
    ('client016@verdora.example', '2026-09-23T11:00:00+00:00', 'DELIVERED', '316.00'),
    ('client017@verdora.example', '2026-09-28T12:00:00+00:00', 'DELIVERED', '69.00'),
    ('client018@verdora.example', '2026-10-03T13:00:00+00:00', 'DELIVERED', '238.00'),
    ('client019@verdora.example', '2026-10-20T10:00:00+00:00', 'PAID', '357.00'),
    ('client020@verdora.example', '2026-10-23T11:00:00+00:00', 'PAID', '476.00'),
    ('client021@verdora.example', '2026-10-26T12:00:00+00:00', 'PAID', '449.00'),
    ('client022@verdora.example', '2026-10-29T13:00:00+00:00', 'PAID', '198.00'),
    ('client023@verdora.example', '2026-11-01T14:00:00+00:00', 'PAID', '1347.00'),
    ('client024@verdora.example', '2026-11-04T15:00:00+00:00', 'PAID', '716.00'),
    ('client025@verdora.example', '2026-10-20T10:00:00+00:00', 'PAID', '299.00'),
    ('client026@verdora.example', '2026-10-23T11:00:00+00:00', 'PAID', '8798.00'),
    ('client027@verdora.example', '2026-10-26T12:00:00+00:00', 'PAID', '195.00'),
    ('client028@verdora.example', '2026-10-29T13:00:00+00:00', 'PAID', '740.00'),
    ('client029@verdora.example', '2026-10-21T09:00:00+00:00', 'SHIPPED', '225.00'),
    ('client030@verdora.example', '2026-10-23T10:00:00+00:00', 'SHIPPED', '138.00'),
    ('client031@verdora.example', '2026-10-25T11:00:00+00:00', 'SHIPPED', '2097.00'),
    ('client032@verdora.example', '2026-10-27T12:00:00+00:00', 'SHIPPED', '1116.00'),
    ('client033@verdora.example', '2026-10-29T13:00:00+00:00', 'SHIPPED', '285.00'),
    ('client034@verdora.example', '2026-10-31T14:00:00+00:00', 'SHIPPED', '338.00'),
    ('client035@verdora.example', '2026-11-02T15:00:00+00:00', 'SHIPPED', '537.00'),
    ('client036@verdora.example', '2026-10-21T09:00:00+00:00', 'SHIPPED', '96.00'),
    ('client037@verdora.example', '2026-10-23T10:00:00+00:00', 'SHIPPED', '449.00'),
    ('client038@verdora.example', '2026-10-24T09:00:00+00:00', 'PENDING', '998.00'),
    ('client039@verdora.example', '2026-10-27T10:00:00+00:00', 'PENDING', '345.00'),
    ('client040@verdora.example', '2026-10-30T11:00:00+00:00', 'PENDING', '396.00'),
    ('client041@verdora.example', '2026-11-02T12:00:00+00:00', 'PENDING', '18.00'),
    ('client042@verdora.example', '2026-11-05T13:00:00+00:00', 'PENDING', '2198.00'),
    ('client043@verdora.example', '2026-10-14T14:00:00+00:00', 'PENDING', '357.00'),
    ('client044@verdora.example', '2026-10-17T15:00:00+00:00', 'PENDING', '1676.00'),
    ('client045@verdora.example', '2026-10-20T16:00:00+00:00', 'PENDING', '329.00'),
    ('client046@verdora.example', '2026-09-01T10:00:00+00:00', 'CANCELLED', '230.00'),
    ('client047@verdora.example', '2026-09-05T11:00:00+00:00', 'CANCELLED', '57.00'),
    ('client048@verdora.example', '2026-09-09T12:00:00+00:00', 'CANCELLED', '3596.00'),
    ('client049@verdora.example', '2026-09-13T13:00:00+00:00', 'CANCELLED', '399.00'),
    ('client050@verdora.example', '2026-09-17T14:00:00+00:00', 'CANCELLED', '298.00')
)
INSERT INTO orders(user_id, created_at, status, total_price)
SELECT u.user_id, s.created_at::timestamptz, s.status, s.total_price::numeric
FROM seed s JOIN users u ON u.email=s.email
WHERE NOT EXISTS (
    SELECT 1 FROM orders o WHERE o.user_id=u.user_id AND o.created_at=s.created_at::timestamptz
);

WITH seed(email, order_created_at, product_name, quantity, price_at_purchase) AS (
    VALUES
    ('client001@verdora.example', '2026-07-10T10:00:00+00:00', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 1, '799.00'),
    ('client002@verdora.example', '2026-07-15T11:00:00+00:00', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 2, '699.00'),
    ('client003@verdora.example', '2026-07-20T12:00:00+00:00', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 3, '699.00'),
    ('client004@verdora.example', '2026-07-25T13:00:00+00:00', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 4, '699.00'),
    ('client005@verdora.example', '2026-07-30T14:00:00+00:00', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 1, '699.00'),
    ('client006@verdora.example', '2026-08-04T15:00:00+00:00', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 2, '799.00'),
    ('client007@verdora.example', '2026-08-09T16:00:00+00:00', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 3, '699.00'),
    ('client008@verdora.example', '2026-08-14T10:00:00+00:00', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 4, '699.00'),
    ('client009@verdora.example', '2026-08-19T11:00:00+00:00', 'Master 20-20-20, водорозчинне добриво 1 кг', 1, '419.00'),
    ('client010@verdora.example', '2026-08-24T12:00:00+00:00', 'Master 20-20-20, водорозчинне добриво 1 кг', 2, '419.00'),
    ('client011@verdora.example', '2026-08-29T13:00:00+00:00', 'Master 20-20-20, водорозчинне добриво 1 кг', 3, '465.00'),
    ('client012@verdora.example', '2026-09-03T14:00:00+00:00', 'Master 20-20-20, водорозчинне добриво 1 кг', 4, '419.00'),
    ('client013@verdora.example', '2026-09-08T15:00:00+00:00', 'Master 20-20-20, водорозчинне добриво 1 кг', 1, '419.00'),
    ('client014@verdora.example', '2026-09-13T16:00:00+00:00', 'Seedera огірок Герман F1, насіння 10 шт', 2, '69.00'),
    ('client015@verdora.example', '2026-09-18T10:00:00+00:00', 'Seedera огірок Герман F1, насіння 10 шт', 3, '69.00'),
    ('client016@verdora.example', '2026-09-23T11:00:00+00:00', 'Seedera огірок Герман F1, насіння 10 шт', 4, '79.00'),
    ('client017@verdora.example', '2026-09-28T12:00:00+00:00', 'Seedera огірок Герман F1, насіння 10 шт', 1, '69.00'),
    ('client018@verdora.example', '2026-10-03T13:00:00+00:00', 'Prosperplast Coubi, горщик антрацит 2 л', 2, '119.00'),
    ('client019@verdora.example', '2026-10-20T10:00:00+00:00', 'Prosperplast Coubi, горщик антрацит 2 л', 3, '119.00'),
    ('client020@verdora.example', '2026-10-23T11:00:00+00:00', 'Prosperplast Coubi, горщик антрацит 2 л', 4, '119.00'),
    ('client021@verdora.example', '2026-10-26T12:00:00+00:00', 'Elho Brussels Round, горщик 20 см', 1, '449.00'),
    ('client022@verdora.example', '2026-10-29T13:00:00+00:00', 'Plantafol 20-20-20, добриво для листкового підживлення 100 г', 2, '99.00'),
    ('client023@verdora.example', '2026-11-01T14:00:00+00:00', 'Osmocote Exact 5-6M, добриво пролонгованої дії 500 г', 3, '449.00'),
    ('client024@verdora.example', '2026-11-04T15:00:00+00:00', 'Доктор Робік 109, засіб для септиків 75 г', 4, '179.00'),
    ('client025@verdora.example', '2026-10-20T10:00:00+00:00', 'Троянда чайно-гібридна у контейнері, 2 л', 1, '299.00'),
    ('client026@verdora.example', '2026-10-23T11:00:00+00:00', 'Kärcher K 2 Universal Edition, мінімийка', 2, '4399.00'),
    ('client027@verdora.example', '2026-10-26T12:00:00+00:00', 'Касета для розсади, 40 комірок', 3, '65.00'),
    ('client028@verdora.example', '2026-10-29T13:00:00+00:00', 'Компост садовий просіяний, 10 л', 4, '185.00'),
    ('client029@verdora.example', '2026-10-21T09:00:00+00:00', 'Агроволокно біле 17 г/м², 3.2×10 м', 1, '225.00'),
    ('client030@verdora.example', '2026-10-23T10:00:00+00:00', 'Seedera огірок Герман F1, насіння 10 шт', 2, '69.00'),
    ('client031@verdora.example', '2026-10-25T11:00:00+00:00', 'Fiskars P121, секатор площинний до 20 мм', 3, '699.00'),
    ('client032@verdora.example', '2026-10-27T12:00:00+00:00', 'Lamela Magnolia, горщик білий 25 см', 4, '279.00'),
    ('client033@verdora.example', '2026-10-29T13:00:00+00:00', 'YaraMila Complex, гранульоване добриво 1 кг', 1, '285.00'),
    ('client034@verdora.example', '2026-10-31T14:00:00+00:00', 'Biopon для квітучих рослин, 500 мл', 2, '169.00'),
    ('client035@verdora.example', '2026-11-02T15:00:00+00:00', 'Біопрепарат для догляду за кореневою системою 250 мл', 3, '179.00'),
    ('client036@verdora.example', '2026-10-21T09:00:00+00:00', 'Seedera айстра Помпонна, суміш 0.3 г', 4, '24.00'),
    ('client037@verdora.example', '2026-10-23T10:00:00+00:00', 'Bradas, крапельна стрічка 16 мм × 50 м', 1, '449.00'),
    ('client038@verdora.example', '2026-10-24T09:00:00+00:00', 'Lamela Finezja, високий вазон 30 см', 2, '499.00'),
    ('client039@verdora.example', '2026-10-27T10:00:00+00:00', 'Вермикомпост для розсади та квітів, 5 л', 3, '115.00'),
    ('client040@verdora.example', '2026-10-30T11:00:00+00:00', 'Клейова стрічка для теплиць, 5 м', 4, '99.00'),
    ('client041@verdora.example', '2026-11-02T12:00:00+00:00', 'Seedera томат Де Барао червоний, насіння 0.1 г', 1, '18.00'),
    ('client042@verdora.example', '2026-11-05T13:00:00+00:00', 'Fiskars Solid, лопата штикова 117 см', 2, '1099.00'),
    ('client043@verdora.example', '2026-10-14T14:00:00+00:00', 'Prosperplast Coubi, горщик антрацит 2 л', 3, '119.00'),
    ('client044@verdora.example', '2026-10-17T15:00:00+00:00', 'Master 20-20-20, водорозчинне добриво 1 кг', 4, '419.00'),
    ('client045@verdora.example', '2026-10-20T16:00:00+00:00', 'Florovit для троянд, гранульоване 1 кг', 1, '329.00'),
    ('client046@verdora.example', '2026-09-01T10:00:00+00:00', 'Садова паста для захисту зрізів 150 г', 2, '115.00'),
    ('client047@verdora.example', '2026-09-05T11:00:00+00:00', 'Яскрава чорнобривці низькорослі, насіння 0.5 г', 3, '19.00'),
    ('client048@verdora.example', '2026-09-09T12:00:00+00:00', 'Gardena Classic, стартовий набір для поливу 1/2', 4, '899.00'),
    ('client049@verdora.example', '2026-09-13T13:00:00+00:00', 'Prosperplast Rato Square, кашпо 26 см', 1, '399.00'),
    ('client050@verdora.example', '2026-09-17T14:00:00+00:00', 'Біогумус універсальний, рідкий концентрат 1 л', 2, '149.00')
)
INSERT INTO order_items(order_id, product_id, quantity, price_at_purchase)
SELECT o.order_id, p.product_id, s.quantity, s.price_at_purchase::numeric
FROM seed s
JOIN users u ON u.email=s.email
JOIN orders o ON o.user_id=u.user_id AND o.created_at=s.order_created_at::timestamptz
JOIN products p ON p.name=s.product_name
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi WHERE oi.order_id=o.order_id AND oi.product_id=p.product_id
);
