-- VERDORA SHOWCASE DATA (synthetic). PostgreSQL / Flyway.
-- Production Flyway migration: applied by default from src/main/resources/db/migration.
-- Do not use real personal information in showcase data.

-- 18 fictional customers. Emails use reserved .example domain, phone values are synthetic.
-- Every customer has an independent, randomly generated bcrypt hash; no known login passwords.
-- Existing admin and all existing real users stay untouched.
WITH seed(name, email, phone_number, password_hash, registered_at) AS (
    VALUES
    ('Олена Коваленко', 'olena.kovalenko@verdora.example', '+380000000100', '$2a$10$wgXiUM6esZD4FYAUN4IPl.HlToULk6MclRxBMYzkwLVc0hiqhAzJ6', '2026-04-05T09:00:00+00:00'::timestamptz),
    ('Андрій Мельник', 'andrii.melnyk@verdora.example', '+380000000101', '$2a$10$vFVt3wyT2M8ZQ2bZy0j8qu2FxSKXHUGvuW0fOIkkkf7Om8a.OQHYK', '2026-04-14T10:07:00+00:00'::timestamptz),
    ('Марія Шевченко', 'mariia.shevchenko@verdora.example', NULL, '$2a$10$uSwZJckB4/XbLYo8OZn.Zu4Bgwj8D28CweKZVgWVKeoJICJ0cePe.', '2026-04-23T11:14:00+00:00'::timestamptz),
    ('Дмитро Бондар', 'dmytro.bondar@verdora.example', '+380000000103', '$2a$10$GpBp4RKAs8qNRL6CjBUFmu4x7jvMJcCJCq45vbio7/QiGF7wWGQH2', '2026-05-02T12:21:00+00:00'::timestamptz),
    ('Катерина Савчук', 'kateryna.savchuk@verdora.example', '+380000000104', '$2a$10$vtRALgeP/fA17nLCWBx0Ped878AS2not2zT3hhcX8cqwQrnoiJd0m', '2026-05-11T13:28:00+00:00'::timestamptz),
    ('Роман Петренко', 'roman.petrenko@verdora.example', '+380000000105', '$2a$10$2LPT8S4lL.WSrsZ40dTSK.STVwwJqzaSLKsDP93hCqsYvQyG3R9jm', '2026-05-20T14:35:00+00:00'::timestamptz),
    ('Софія Гриценко', 'sofia.hrytsenko@verdora.example', NULL, '$2a$10$DBhNQDYVMxON3NxeLM3.8OrAtcRRc8q/s5piJjA35.dCqOfh0UOnG', '2026-05-29T09:42:00+00:00'::timestamptz),
    ('Михайло Ткаченко', 'mykhailo.tkachenko@verdora.example', '+380000000107', '$2a$10$/jAj3A77oxo/zYDM3SxmT.lG3l7Ervt0jfP9x38Cx9ZWiY05/wWXW', '2026-06-07T10:49:00+00:00'::timestamptz),
    ('Юлія Кравчук', 'yuliia.kravchuk@verdora.example', '+380000000108', '$2a$10$cDHuVHHW1JVS.7TZTsEeFuGAoF8NacUrRZbllBwDbQKKd0fsJbGGK', '2026-06-16T11:56:00+00:00'::timestamptz),
    ('Тарас Коваль', 'taras.koval@verdora.example', '+380000000109', '$2a$10$yvLRVU2OUQbNFWa/Sf7uvOnGaJmroirkKH2c4XP2GaulR8UVsp/0a', '2026-06-25T12:03:00+00:00'::timestamptz),
    ('Ірина Поліщук', 'iryna.polishchuk@verdora.example', '+380000000110', '$2a$10$BC91gk7vze..H0eJ6wlQwOMMTYBZpmruZenYs5jQzIa/31Yo0IBmm', '2026-07-04T13:10:00+00:00'::timestamptz),
    ('Олексій Левченко', 'oleksii.levchenko@verdora.example', '+380000000111', '$2a$10$xN948qMmM6AfJTtZYsYGOeLBX475KSvlGj/pSj3.PC4hv48qd6Ecy', '2026-07-13T14:17:00+00:00'::timestamptz),
    ('Анастасія Романюк', 'anastasiia.romaniuk@verdora.example', NULL, '$2a$10$9ttz2THDJnuBxl9aZ7ipkOQAd1XxhoQQ3UZequgYUKqGLDrg9jbbK', '2026-07-22T09:24:00+00:00'::timestamptz),
    ('Богдан Мороз', 'bohdan.moroz@verdora.example', '+380000000113', '$2a$10$ALVooyDgvtrCo/wbuobOd.B/OvnhbfeJPzAv/QirwDkeyxB93IESm', '2026-07-31T10:31:00+00:00'::timestamptz),
    ('Наталія Іванчук', 'nataliia.ivanchuk@verdora.example', '+380000000114', '$2a$10$JC4LaoB8s8Bj6VzVLG.auuNK1CyR575jWsx1SiV.zXrQXJ3w388DW', '2026-08-09T11:38:00+00:00'::timestamptz),
    ('Владислав Козак', 'vladyslav.kozak@verdora.example', '+380000000115', '$2a$10$ct/ZyX63nLvC1QIz.tFPKutXu27w/GQoYNYGEHLmEtl5DAiWHV.jW', '2026-08-18T12:45:00+00:00'::timestamptz),
    ('Вікторія Лисенко', 'viktoriia.lysenko@verdora.example', NULL, '$2a$10$ADUlQgrCaDlF62Dg/Aj0uuIybBfjKbuQqX605pCwQ76DU4PzAPH6q', '2026-08-27T13:52:00+00:00'::timestamptz),
    ('Максим Данилюк', 'maksym.danyliuk@verdora.example', '+380000000117', '$2a$10$8BESe60OGLMg8hqAU6l0Du1fR0.yRGT6RQXTvkkDYygDUDhBwRQ3e', '2026-09-05T14:59:00+00:00'::timestamptz)
)
INSERT INTO users(name, email, phone_number, password_hash, role, created_at, updated_at)
SELECT s.name, s.email, s.phone_number, s.password_hash, 'USER', s.registered_at, s.registered_at
FROM seed s
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.email = s.email);
