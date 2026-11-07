-- VERDORA GARDEN SHOWCASE DATA | synthetic records for a public portfolio deployment.
-- Product prices are illustrative UAH values; customer contact details are fictitious.
-- PostgreSQL / Flyway; migration runs exactly once on a fresh database.

-- 50 favorites and 50 used, expired password-reset tokens.
-- Reset tokens have no value for authentication and cannot be used for password reset.
WITH seed(email, product_name, created_at) AS (
    VALUES
    ('client008@verdora.example', 'Osmocote Exact 5-6M, добриво пролонгованої дії 500 г', '2026-08-10T09:00:00+00:00'),
    ('client021@verdora.example', 'Seedera айстра Помпонна, суміш 0.3 г', '2026-08-15T10:00:00+00:00'),
    ('client034@verdora.example', 'Prosperplast Rato Square, кашпо 26 см', '2026-08-20T11:00:00+00:00'),
    ('client047@verdora.example', 'Жовті клейові пастки для комах, комплект 10 шт', '2026-08-25T12:00:00+00:00'),
    ('client010@verdora.example', 'Троянда чайно-гібридна у контейнері, 2 л', '2026-08-30T13:00:00+00:00'),
    ('client023@verdora.example', 'Lamela Finezja, високий вазон 30 см', '2026-09-04T14:00:00+00:00'),
    ('client036@verdora.example', 'Сітка захисна від комах для грядок, 2×5 м', '2026-09-09T15:00:00+00:00'),
    ('client049@verdora.example', 'Лаванда вузьколиста Hidcote, контейнер P9', '2026-09-16T17:24:00+00:00'),
    ('client012@verdora.example', 'Касета для розсади, 40 комірок', '2026-09-19T09:00:00+00:00'),
    ('client025@verdora.example', 'Клейова стрічка для теплиць, 5 м', '2026-09-24T10:00:00+00:00'),
    ('client038@verdora.example', 'Цибулини тюльпанів Triumph, мікс 10 шт', '2026-08-12T11:00:00+00:00'),
    ('client001@verdora.example', 'Торф’яні горщики для розсади 8 см, 20 шт', '2026-08-17T12:00:00+00:00'),
    ('client014@verdora.example', 'Агроволокно біле 17 г/м², 3.2×10 м', '2026-08-22T13:00:00+00:00'),
    ('client027@verdora.example', 'Fiskars Solid, лопата штикова 117 см', '2026-08-27T14:00:00+00:00'),
    ('client040@verdora.example', 'Мініпарник для розсади з кришкою, 50×20 см', '2026-09-01T15:00:00+00:00'),
    ('client003@verdora.example', 'Фітоспорин-М, біопрепарат для рослин 10 г', '2026-09-06T16:00:00+00:00'),
    ('client016@verdora.example', 'Fiskars P121, секатор площинний до 20 мм', '2026-09-11T09:00:00+00:00'),
    ('client029@verdora.example', 'Master 20-20-20, водорозчинне добриво 1 кг', '2026-09-16T10:00:00+00:00'),
    ('client042@verdora.example', 'Триходерма, біозасіб для ґрунту 100 мл', '2026-09-21T11:00:00+00:00'),
    ('client005@verdora.example', 'Gardena Classic, садовий совок 8.5 см', '2026-09-26T12:00:00+00:00'),
    ('client018@verdora.example', 'YaraMila Complex, гранульоване добриво 1 кг', '2026-08-14T13:00:00+00:00'),
    ('client031@verdora.example', 'Садова паста для захисту зрізів 150 г', '2026-08-19T14:00:00+00:00'),
    ('client044@verdora.example', 'Cellfast Economic, шланг 1/2 дюйма 20 м', '2026-08-24T15:00:00+00:00'),
    ('client007@verdora.example', 'Plantafol 20-20-20, добриво для листкового підживлення 100 г', '2026-08-29T16:00:00+00:00'),
    ('client020@verdora.example', 'Біопрепарат для догляду за кореневою системою 250 мл', '2026-09-03T09:00:00+00:00'),
    ('client033@verdora.example', 'Gardena Classic, стартовий набір для поливу 1/2', '2026-09-08T10:00:00+00:00'),
    ('client046@verdora.example', 'Agrecol для газону Весна-Літо, 3 кг', '2026-09-13T11:00:00+00:00'),
    ('client009@verdora.example', 'Доктор Робік 109, засіб для септиків 75 г', '2026-09-18T12:00:00+00:00'),
    ('client022@verdora.example', 'Bradas, крапельна стрічка 16 мм × 50 м', '2026-09-23T13:00:00+00:00'),
    ('client035@verdora.example', 'Біогумус універсальний, рідкий концентрат 1 л', '2026-08-11T14:00:00+00:00'),
    ('client048@verdora.example', 'Біоактиватор для компосту, 100 г', '2026-08-16T15:00:00+00:00'),
    ('client011@verdora.example', 'Kärcher K 2 Universal Edition, мінімийка', '2026-08-21T16:00:00+00:00'),
    ('client024@verdora.example', 'Вермикомпост для розсади та квітів, 5 л', '2026-08-26T09:00:00+00:00'),
    ('client037@verdora.example', 'Біопрепарат для вигрібних ям і септиків, 500 г', '2026-08-31T10:00:00+00:00'),
    ('client050@verdora.example', 'Gardena EasyCut 450/25, електричний тример', '2026-09-11T15:37:00+00:00'),
    ('client013@verdora.example', 'Компост садовий просіяний, 10 л', '2026-09-10T12:00:00+00:00'),
    ('client026@verdora.example', 'Seedera томат Де Барао червоний, насіння 0.1 г', '2026-09-15T13:00:00+00:00'),
    ('client039@verdora.example', 'AL-KO Comfort 38.2 E, електрична газонокосарка', '2026-09-20T14:00:00+00:00'),
    ('client002@verdora.example', 'Гумат калію для саду та городу, 500 мл', '2026-09-25T15:00:00+00:00'),
    ('client015@verdora.example', 'Seedera огірок Герман F1, насіння 10 шт', '2026-08-13T16:00:00+00:00'),
    ('client028@verdora.example', 'Prosperplast Coubi, горщик антрацит 2 л', '2026-08-18T09:00:00+00:00'),
    ('client041@verdora.example', 'COMPO добриво для орхідей, 250 мл', '2026-08-23T10:00:00+00:00'),
    ('client004@verdora.example', 'Яскрава морква Нантська, насіння 2 г', '2026-08-28T11:00:00+00:00'),
    ('client017@verdora.example', 'Lamela Magnolia, горщик білий 25 см', '2026-09-02T12:00:00+00:00'),
    ('client030@verdora.example', 'Florovit для троянд, гранульоване 1 кг', '2026-09-07T13:00:00+00:00'),
    ('client043@verdora.example', 'Seedera петунія великоквіткова, суміш 0.1 г', '2026-09-12T14:00:00+00:00'),
    ('client006@verdora.example', 'Elho Brussels Round, горщик 20 см', '2026-09-17T15:00:00+00:00'),
    ('client019@verdora.example', 'Biopon для квітучих рослин, 500 мл', '2026-09-22T16:00:00+00:00'),
    ('client032@verdora.example', 'Яскрава чорнобривці низькорослі, насіння 0.5 г', '2026-08-10T09:00:00+00:00'),
    ('client045@verdora.example', 'Lechuza Classico Color 21, кашпо з автополивом', '2026-08-15T10:00:00+00:00')
)
INSERT INTO favorites(user_id, product_id, created_at)
SELECT u.user_id, p.product_id, s.created_at::timestamptz
FROM seed s JOIN users u ON u.email=s.email
JOIN products p ON p.name=s.product_name
WHERE NOT EXISTS (
    SELECT 1 FROM favorites f WHERE f.user_id=u.user_id AND f.product_id=p.product_id
);

WITH seed(email, token, created_at, expires_at) AS (
    VALUES
    ('client001@verdora.example', 'showcase-used-79f22a4697e62ab6ccc3aba48419617f998256bce0efed408965e5786493abf9', '2026-09-02T11:00:00', '2026-09-02T12:00:00'),
    ('client002@verdora.example', 'showcase-used-b6b87b11b0d71f291bacc7797fffa23613e9dc445780d1777e130e28ea041136', '2026-09-03T14:00:00', '2026-09-03T15:00:00'),
    ('client003@verdora.example', 'showcase-used-57e8fcdc3d3f5e8dbf1d01452c5749300f0b341553855cbd666e0a5abd993526', '2026-09-04T17:00:00', '2026-09-04T18:00:00'),
    ('client004@verdora.example', 'showcase-used-873acc7a1dd5d18ea32525bc7ae75f0d3a99e0aba7e1d1df68e2bc0c08419d9b', '2026-09-05T20:00:00', '2026-09-05T21:00:00'),
    ('client005@verdora.example', 'showcase-used-4654e3d3d68cf3358368b6ac43278268c604ab8f917eafc90c81d32da46a77fe', '2026-09-06T12:00:00', '2026-09-06T13:00:00'),
    ('client006@verdora.example', 'showcase-used-4265b0186beb54d829978e493453666440a1fdbc5be19e31c83ee44a77a25433', '2026-09-07T15:00:00', '2026-09-07T16:00:00'),
    ('client007@verdora.example', 'showcase-used-de3ea6b1ce656a559909a3583452b77974c30aae04c9c69355d306c3a0b94934', '2026-09-08T18:00:00', '2026-09-08T19:00:00'),
    ('client008@verdora.example', 'showcase-used-5448d36f98bef2ebacc90bdf062a8dc4bb56085610194364a6aeca6d3502b4e0', '2026-09-09T21:00:00', '2026-09-09T22:00:00'),
    ('client009@verdora.example', 'showcase-used-ce6f227d066fbae0678d31d880208deeae075959f46f07a11fe1d607be0ec89d', '2026-09-10T13:00:00', '2026-09-10T14:00:00'),
    ('client010@verdora.example', 'showcase-used-d007a374e2cda94919adf67352a760f3117378b82cdf49a668fa743520d598a2', '2026-09-11T16:00:00', '2026-09-11T17:00:00'),
    ('client011@verdora.example', 'showcase-used-e14f066a4e93438d9dd8dd945cf0e21b7354ce52069695fb65203ab507491bf8', '2026-09-12T19:00:00', '2026-09-12T20:00:00'),
    ('client012@verdora.example', 'showcase-used-6c8ff01fe7e76d4a124e01e4874b668563b376aeb2d80661ba8536165f8c8803', '2026-09-13T11:00:00', '2026-09-13T12:00:00'),
    ('client013@verdora.example', 'showcase-used-e46ace5399090ddf2770b1264a13edea4e7e27a398034e2d8b4f3daef8c6a653', '2026-09-14T14:00:00', '2026-09-14T15:00:00'),
    ('client014@verdora.example', 'showcase-used-3d8f2b374accffa5eb4163ddc7af16cc69430976c788ce13bc0f565ca62a6226', '2026-09-15T17:00:00', '2026-09-15T18:00:00'),
    ('client015@verdora.example', 'showcase-used-e01f4efe1ef009617b48158594b0ba21dcc3bb7de9096c76548eab2a86eba325', '2026-09-16T20:00:00', '2026-09-16T21:00:00'),
    ('client016@verdora.example', 'showcase-used-5eda1fac079934e0629973363086618adc6438a18498e55156b8f32c77a9426e', '2026-09-17T12:00:00', '2026-09-17T13:00:00'),
    ('client017@verdora.example', 'showcase-used-4dd11cc7a9efb351dac7e6f12cd1f2f066e9ced72db9fdf53f8ea48784430cb1', '2026-09-18T15:00:00', '2026-09-18T16:00:00'),
    ('client018@verdora.example', 'showcase-used-05e80127a70aef6378adce892a9310c7d929e727ba7036f368a1030e8b12e63c', '2026-09-19T18:00:00', '2026-09-19T19:00:00'),
    ('client019@verdora.example', 'showcase-used-b6cc248aaa3765d1fb9661fc4c284ce8437bd15364acbaa086eba1f686306424', '2026-09-20T21:00:00', '2026-09-20T22:00:00'),
    ('client020@verdora.example', 'showcase-used-c69737063b87e292d3a843e485a9dd6cf2af5072d91b5a9bc44a73353b8f6bfa', '2026-09-21T13:00:00', '2026-09-21T14:00:00'),
    ('client021@verdora.example', 'showcase-used-761a39d6cc72d7a3f9a8ffc4523c70a906917237651263b6587cbeb442ac0972', '2026-09-22T16:00:00', '2026-09-22T17:00:00'),
    ('client022@verdora.example', 'showcase-used-64548d5840fa4d754715f5ac4f42937aa732911b6683c17542b658880aa7d301', '2026-09-23T19:00:00', '2026-09-23T20:00:00'),
    ('client023@verdora.example', 'showcase-used-dac3798424109e44d64a2c664dd6f8c1019345c705544885bcb3f8b1e6f33fec', '2026-09-24T11:00:00', '2026-09-24T12:00:00'),
    ('client024@verdora.example', 'showcase-used-cf6fc09b1ae5363fa1dc95d3e9bb371f74b53f3670ba1bcf6d2e1eae651dc973', '2026-09-25T14:00:00', '2026-09-25T15:00:00'),
    ('client025@verdora.example', 'showcase-used-46fcc577c908b4fcf11de03f842891117f8c2ef9bbfcd212e10882ceb2d4570b', '2026-09-26T17:00:00', '2026-09-26T18:00:00'),
    ('client026@verdora.example', 'showcase-used-a26e6b0e772db1c38e4e4c67db0b9eb50d3e47f7b45ff79934d4a3ba7bc912a7', '2026-09-27T20:00:00', '2026-09-27T21:00:00'),
    ('client027@verdora.example', 'showcase-used-ad5f1cdcc895c55d65ebb751a726e7b0c9036b03ad0d902402771286ffaf4acf', '2026-09-28T12:00:00', '2026-09-28T13:00:00'),
    ('client028@verdora.example', 'showcase-used-2e8ebbfaee8302d026005855dcf19a7e91a2844d561d193fa95b07baa7852da3', '2026-09-29T15:00:00', '2026-09-29T16:00:00'),
    ('client029@verdora.example', 'showcase-used-58186740dffb63d4ef4082ff9eec7fecb478e7add07dbdfd5c11a70a44e5868f', '2026-09-30T18:00:00', '2026-09-30T19:00:00'),
    ('client030@verdora.example', 'showcase-used-7a12adf97449086687753acef875b8a34eb18930424803dc682e0a220881d7e1', '2026-10-01T21:00:00', '2026-10-01T22:00:00'),
    ('client031@verdora.example', 'showcase-used-2c182a63aa809bccc78ceaab66c626412dd55c329349d1f6abf341daf63ae92a', '2026-10-02T13:00:00', '2026-10-02T14:00:00'),
    ('client032@verdora.example', 'showcase-used-6892a14514375e3857a1621eaa3a7647f378ee88299f6f05137edae777800ebc', '2026-10-03T16:00:00', '2026-10-03T17:00:00'),
    ('client033@verdora.example', 'showcase-used-80a53cb83141bb3afcd4536337d170ce3096337200b11f8f8c55d4f323ed0e1d', '2026-10-04T19:00:00', '2026-10-04T20:00:00'),
    ('client034@verdora.example', 'showcase-used-0678cde1da6f4d1d3d9883e3ed4fd115b8573b9c93a25c6df62a692849afac2b', '2026-10-05T11:00:00', '2026-10-05T12:00:00'),
    ('client035@verdora.example', 'showcase-used-f2537ce254bf284cfadddf7971acbe14b3742b12ebd9af4715f5b070680de09b', '2026-10-06T14:00:00', '2026-10-06T15:00:00'),
    ('client036@verdora.example', 'showcase-used-c0f617a11db43adabf5096ddcaeac4ac7017d5fa75ed3a6b1fb8d59d622c97fc', '2026-10-07T17:00:00', '2026-10-07T18:00:00'),
    ('client037@verdora.example', 'showcase-used-836f935f68994daa7318463649cdd54e5984c2df0d3b62e760005512b2e6eec2', '2026-10-08T20:00:00', '2026-10-08T21:00:00'),
    ('client038@verdora.example', 'showcase-used-0710bd5f3a164b5657638ade9083f3f4e3203df0f35489eeebb4871f0ce74677', '2026-09-02T12:00:00', '2026-09-02T13:00:00'),
    ('client039@verdora.example', 'showcase-used-7166fde8424b9551785fccbc700bff9f6ce70fce1d1e9e5c5ee501977e95e556', '2026-09-03T15:00:00', '2026-09-03T16:00:00'),
    ('client040@verdora.example', 'showcase-used-5dc529d72ff441401cf8d82ee69f8e114bcf18acff89c14284f33d5bb2bb471d', '2026-09-04T18:00:00', '2026-09-04T19:00:00'),
    ('client041@verdora.example', 'showcase-used-4d36b4bed4f7a29e5a0904c87c7943d593ee442d675fdeb0a1a83b7261ca9d23', '2026-09-05T21:00:00', '2026-09-05T22:00:00'),
    ('client042@verdora.example', 'showcase-used-5315927a1e68bf5e28d45c8ca700f76a8db7840c96a563a4a7ef40dd26b1bb71', '2026-09-06T13:00:00', '2026-09-06T14:00:00'),
    ('client043@verdora.example', 'showcase-used-00a4d8bb8543f67c2252ee5229de69b1c0f524a8845dadff1e3cefbd6a8f4358', '2026-09-07T16:00:00', '2026-09-07T17:00:00'),
    ('client044@verdora.example', 'showcase-used-f9872d0f463bb9721876bed44806fb9cff2831fa86d032a20b0f34c7926b880b', '2026-09-08T19:00:00', '2026-09-08T20:00:00'),
    ('client045@verdora.example', 'showcase-used-cee28f11b5ac89a3ea12df71b76eee6ce83754507807f8a68e39b9ac7d0bdc44', '2026-09-09T11:00:00', '2026-09-09T12:00:00'),
    ('client046@verdora.example', 'showcase-used-8107cbd975b3d37998b8dcfa29f2dbc088dacfcd4779bb58a428f3371fdd65b8', '2026-09-10T14:00:00', '2026-09-10T15:00:00'),
    ('client047@verdora.example', 'showcase-used-3b395d7aff184aa488dbad6b49dab655c94fb02cacffdfbc480e1fd02f9a8ad1', '2026-09-11T17:00:00', '2026-09-11T18:00:00'),
    ('client048@verdora.example', 'showcase-used-a5a01306607af6430a825df2d97db4a9d1f1aa685966473df9d023608da3cc20', '2026-09-12T20:00:00', '2026-09-12T21:00:00'),
    ('client049@verdora.example', 'showcase-used-9efc37965fbd7eac75d96ea9542a22e9822d8c7b70057a3ca4cadbd6a1a38d3d', '2026-09-13T12:00:00', '2026-09-13T13:00:00'),
    ('client050@verdora.example', 'showcase-used-9797c52fb9886ca7c586485596f72dca9810dc55e6b45627f91059dd98149ab9', '2026-09-14T15:00:00', '2026-09-14T16:00:00')
)
INSERT INTO password_reset_tokens(user_id, token, created_at, expires_at, used)
SELECT u.user_id, s.token, s.created_at::timestamp, s.expires_at::timestamp, TRUE
FROM seed s JOIN users u ON u.email=s.email
WHERE NOT EXISTS (SELECT 1 FROM password_reset_tokens prt WHERE prt.token=s.token);
