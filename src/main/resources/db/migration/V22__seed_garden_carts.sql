-- VERDORA GARDEN SHOWCASE DATA | synthetic records for a public portfolio deployment.
-- Product prices are illustrative UAH values; customer contact details are fictitious.
-- PostgreSQL / Flyway; migration runs exactly once on a fresh database.

-- 50 customer carts, including 10 empty carts, and 50 line items.
WITH seed(email, created_at, updated_at) AS (
    VALUES
    ('client001@verdora.example', '2026-08-01T09:00:00+00:00', '2026-08-01T10:00:00+00:00'),
    ('client002@verdora.example', '2026-08-04T10:00:00+00:00', '2026-08-04T12:00:00+00:00'),
    ('client003@verdora.example', '2026-08-07T11:00:00+00:00', '2026-08-07T14:00:00+00:00'),
    ('client004@verdora.example', '2026-08-10T12:00:00+00:00', '2026-08-10T16:00:00+00:00'),
    ('client005@verdora.example', '2026-08-13T13:00:00+00:00', '2026-08-13T18:00:00+00:00'),
    ('client006@verdora.example', '2026-08-16T14:00:00+00:00', '2026-08-16T15:00:00+00:00'),
    ('client007@verdora.example', '2026-08-19T09:00:00+00:00', '2026-08-19T11:00:00+00:00'),
    ('client008@verdora.example', '2026-08-22T10:00:00+00:00', '2026-08-22T13:00:00+00:00'),
    ('client009@verdora.example', '2026-08-25T11:00:00+00:00', '2026-08-25T15:00:00+00:00'),
    ('client010@verdora.example', '2026-08-28T12:00:00+00:00', '2026-08-28T17:00:00+00:00'),
    ('client011@verdora.example', '2026-08-31T13:00:00+00:00', '2026-08-31T14:00:00+00:00'),
    ('client012@verdora.example', '2026-09-03T14:00:00+00:00', '2026-09-03T16:00:00+00:00'),
    ('client013@verdora.example', '2026-09-06T09:00:00+00:00', '2026-09-06T12:00:00+00:00'),
    ('client014@verdora.example', '2026-09-09T10:00:00+00:00', '2026-09-09T14:00:00+00:00'),
    ('client015@verdora.example', '2026-08-03T11:00:00+00:00', '2026-08-03T16:00:00+00:00'),
    ('client016@verdora.example', '2026-08-06T12:00:00+00:00', '2026-08-06T13:00:00+00:00'),
    ('client017@verdora.example', '2026-08-09T13:00:00+00:00', '2026-08-09T15:00:00+00:00'),
    ('client018@verdora.example', '2026-08-12T14:00:00+00:00', '2026-08-12T17:00:00+00:00'),
    ('client019@verdora.example', '2026-08-15T09:00:00+00:00', '2026-08-15T13:00:00+00:00'),
    ('client020@verdora.example', '2026-08-18T10:00:00+00:00', '2026-08-18T15:00:00+00:00'),
    ('client021@verdora.example', '2026-08-21T11:00:00+00:00', '2026-08-21T12:00:00+00:00'),
    ('client022@verdora.example', '2026-08-24T12:00:00+00:00', '2026-08-24T14:00:00+00:00'),
    ('client023@verdora.example', '2026-08-27T13:00:00+00:00', '2026-08-27T16:00:00+00:00'),
    ('client024@verdora.example', '2026-08-30T14:00:00+00:00', '2026-08-30T18:00:00+00:00'),
    ('client025@verdora.example', '2026-09-02T09:00:00+00:00', '2026-09-02T14:00:00+00:00'),
    ('client026@verdora.example', '2026-09-05T10:00:00+00:00', '2026-09-05T11:00:00+00:00'),
    ('client027@verdora.example', '2026-09-08T11:00:00+00:00', '2026-09-08T13:00:00+00:00'),
    ('client028@verdora.example', '2026-08-02T12:00:00+00:00', '2026-08-02T15:00:00+00:00'),
    ('client029@verdora.example', '2026-08-05T13:00:00+00:00', '2026-08-05T17:00:00+00:00'),
    ('client030@verdora.example', '2026-08-08T14:00:00+00:00', '2026-08-08T19:00:00+00:00'),
    ('client031@verdora.example', '2026-08-11T09:00:00+00:00', '2026-08-11T10:00:00+00:00'),
    ('client032@verdora.example', '2026-08-14T10:00:00+00:00', '2026-08-14T12:00:00+00:00'),
    ('client033@verdora.example', '2026-08-17T11:00:00+00:00', '2026-08-17T14:00:00+00:00'),
    ('client034@verdora.example', '2026-08-20T12:00:00+00:00', '2026-08-20T16:00:00+00:00'),
    ('client035@verdora.example', '2026-08-23T13:00:00+00:00', '2026-08-23T18:00:00+00:00'),
    ('client036@verdora.example', '2026-08-26T14:00:00+00:00', '2026-08-26T15:00:00+00:00'),
    ('client037@verdora.example', '2026-08-29T09:00:00+00:00', '2026-08-29T11:00:00+00:00'),
    ('client038@verdora.example', '2026-09-01T10:00:00+00:00', '2026-09-01T13:00:00+00:00'),
    ('client039@verdora.example', '2026-09-04T11:00:00+00:00', '2026-09-04T15:00:00+00:00'),
    ('client040@verdora.example', '2026-09-07T12:00:00+00:00', '2026-09-07T17:00:00+00:00'),
    ('client041@verdora.example', '2026-08-01T13:00:00+00:00', '2026-08-01T14:00:00+00:00'),
    ('client042@verdora.example', '2026-08-04T14:00:00+00:00', '2026-08-04T16:00:00+00:00'),
    ('client043@verdora.example', '2026-08-07T09:00:00+00:00', '2026-08-07T12:00:00+00:00'),
    ('client044@verdora.example', '2026-08-10T10:00:00+00:00', '2026-08-10T14:00:00+00:00'),
    ('client045@verdora.example', '2026-08-13T11:00:00+00:00', '2026-08-13T16:00:00+00:00'),
    ('client046@verdora.example', '2026-08-16T12:00:00+00:00', '2026-08-16T13:00:00+00:00'),
    ('client047@verdora.example', '2026-08-22T19:58:00+00:00', '2026-08-22T21:58:00+00:00'),
    ('client048@verdora.example', '2026-08-29T23:11:00+00:00', '2026-08-30T02:11:00+00:00'),
    ('client049@verdora.example', '2026-09-05T10:24:00+00:00', '2026-09-05T14:24:00+00:00'),
    ('client050@verdora.example', '2026-09-12T14:37:00+00:00', '2026-09-12T19:37:00+00:00')
)
INSERT INTO carts(user_id, created_at, updated_at)
SELECT u.user_id, s.created_at::timestamptz, s.updated_at::timestamptz
FROM seed s JOIN users u ON u.email = s.email
WHERE NOT EXISTS (SELECT 1 FROM carts c WHERE c.user_id = u.user_id);

WITH seed(email, product_name, quantity) AS (
    VALUES
    ('client001@verdora.example', 'COMPO добриво для орхідей, 250 мл', 1),
    ('client002@verdora.example', 'Агроволокно біле 17 г/м², 3.2×10 м', 2),
    ('client003@verdora.example', 'Біопрепарат для вигрібних ям і септиків, 500 г', 3),
    ('client004@verdora.example', 'Троянда чайно-гібридна у контейнері, 2 л', 4),
    ('client005@verdora.example', 'Gardena Classic, стартовий набір для поливу 1/2', 1),
    ('client006@verdora.example', 'Elho Brussels Round, горщик 20 см', 2),
    ('client007@verdora.example', 'Master 20-20-20, водорозчинне добриво 1 кг', 3),
    ('client008@verdora.example', 'Гумат калію для саду та городу, 500 мл', 4),
    ('client009@verdora.example', 'Клейова стрічка для теплиць, 5 м', 1),
    ('client010@verdora.example', 'Біоактиватор для компосту, 100 г', 2),
    ('client011@verdora.example', 'Seedera айстра Помпонна, суміш 0.3 г', 3),
    ('client012@verdora.example', 'Cellfast Economic, шланг 1/2 дюйма 20 м', 4),
    ('client013@verdora.example', 'Lamela Magnolia, горщик білий 25 см', 1),
    ('client014@verdora.example', 'Мініпарник для розсади з кришкою, 50×20 см', 2),
    ('client015@verdora.example', 'Компост садовий просіяний, 10 л', 3),
    ('client016@verdora.example', 'Сітка захисна від комах для грядок, 2×5 м', 4),
    ('client017@verdora.example', 'Доктор Робік 109, засіб для септиків 75 г', 1),
    ('client018@verdora.example', 'Яскрава чорнобривці низькорослі, насіння 0.5 г', 2),
    ('client019@verdora.example', 'Gardena Classic, садовий совок 8.5 см', 3),
    ('client020@verdora.example', 'Prosperplast Coubi, горщик антрацит 2 л', 4),
    ('client021@verdora.example', 'Торф’яні горщики для розсади 8 см, 20 шт', 1),
    ('client022@verdora.example', 'Вермикомпост для розсади та квітів, 5 л', 2),
    ('client023@verdora.example', 'Жовті клейові пастки для комах, комплект 10 шт', 3),
    ('client024@verdora.example', 'Біопрепарат для догляду за кореневою системою 250 мл', 4),
    ('client025@verdora.example', 'Seedera петунія великоквіткова, суміш 0.1 г', 1),
    ('client026@verdora.example', 'Fiskars P121, секатор площинний до 20 мм', 2),
    ('client027@verdora.example', 'AL-KO Comfort 38.2 E, електрична газонокосарка', 3),
    ('client028@verdora.example', 'Касета для розсади, 40 комірок', 4),
    ('client029@verdora.example', 'Біогумус універсальний, рідкий концентрат 1 л', 1),
    ('client030@verdora.example', 'Osmocote Exact 5-6M, добриво пролонгованої дії 500 г', 2),
    ('client031@verdora.example', 'Садова паста для захисту зрізів 150 г', 3),
    ('client032@verdora.example', 'Яскрава морква Нантська, насіння 2 г', 4),
    ('client033@verdora.example', 'Fiskars Solid, лопата штикова 117 см', 1),
    ('client034@verdora.example', 'Gardena EasyCut 450/25, електричний тример', 2),
    ('client035@verdora.example', 'Lamela Finezja, високий вазон 30 см', 3),
    ('client036@verdora.example', 'Agrecol для газону Весна-Літо, 3 кг', 4),
    ('client037@verdora.example', 'Biopon для квітучих рослин, 500 мл', 1),
    ('client038@verdora.example', 'Триходерма, біозасіб для ґрунту 100 мл', 2),
    ('client039@verdora.example', 'Seedera огірок Герман F1, насіння 10 шт', 3),
    ('client040@verdora.example', 'Цибулини тюльпанів Triumph, мікс 10 шт', 4),
    ('client001@verdora.example', 'Яскрава чорнобривці низькорослі, насіння 0.5 г', 2),
    ('client002@verdora.example', 'Gardena Classic, садовий совок 8.5 см', 3),
    ('client003@verdora.example', 'Prosperplast Coubi, горщик антрацит 2 л', 4),
    ('client004@verdora.example', 'Торф’яні горщики для розсади 8 см, 20 шт', 2),
    ('client005@verdora.example', 'Вермикомпост для розсади та квітів, 5 л', 3),
    ('client006@verdora.example', 'Жовті клейові пастки для комах, комплект 10 шт', 4),
    ('client007@verdora.example', 'Біопрепарат для догляду за кореневою системою 250 мл', 2),
    ('client008@verdora.example', 'Seedera петунія великоквіткова, суміш 0.1 г', 3),
    ('client009@verdora.example', 'Fiskars P121, секатор площинний до 20 мм', 4),
    ('client010@verdora.example', 'AL-KO Comfort 38.2 E, електрична газонокосарка', 2)
)
INSERT INTO cart_items(cart_id, product_id, quantity)
SELECT c.cart_id, p.product_id, s.quantity
FROM seed s
JOIN users u ON u.email = s.email
JOIN carts c ON c.user_id = u.user_id
JOIN products p ON p.name = s.product_name
WHERE NOT EXISTS (
    SELECT 1 FROM cart_items ci WHERE ci.cart_id = c.cart_id AND ci.product_id = p.product_id
);
