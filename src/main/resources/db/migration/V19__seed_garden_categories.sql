-- VERDORA GARDEN SHOWCASE DATA | synthetic records for a public portfolio deployment.
-- Product prices are illustrative UAH values; customer contact details are fictitious.
-- PostgreSQL / Flyway; migration runs exactly once on a fresh database.

-- 15 categories across five business areas: fertilizer, protection, planting,
-- tools/equipment, pots/planters. The database has no parent-category column.
WITH seed(name, image_url) AS (
    VALUES
    ('Мінеральні добрива', 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?auto=format&fit=crop&w=900&q=85'),
    ('Органічні добрива', 'https://images.unsplash.com/photo-1466692476868-aef1dfb1e735?auto=format&fit=crop&w=900&q=85'),
    ('Добрива для квітів', 'https://images.unsplash.com/photo-1485955900006-10f4d324d411?auto=format&fit=crop&w=900&q=85'),
    ('Захист від шкідників', 'https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=900&q=85'),
    ('Захист від хвороб рослин', 'https://images.unsplash.com/photo-1525498128493-380d1990a112?auto=format&fit=crop&w=900&q=85'),
    ('Засоби для септиків і компосту', 'https://images.unsplash.com/photo-1497250681960-ef046c08a56e?auto=format&fit=crop&w=900&q=85'),
    ('Насіння овочів', 'https://images.unsplash.com/photo-1463320726281-696a485928c7?auto=format&fit=crop&w=900&q=85'),
    ('Насіння квітів', 'https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=900&q=85'),
    ('Саджанці та цибулини', 'https://images.unsplash.com/photo-1483794344563-d27a8d18014e?auto=format&fit=crop&w=900&q=85'),
    ('Ручний садовий інструмент', 'https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?auto=format&fit=crop&w=900&q=85'),
    ('Полив і зрошення', 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?auto=format&fit=crop&w=900&q=85'),
    ('Садова техніка', 'https://images.unsplash.com/photo-1466692476868-aef1dfb1e735?auto=format&fit=crop&w=900&q=85'),
    ('Горщики для рослин', 'https://images.unsplash.com/photo-1459156212016-c812468e2115?auto=format&fit=crop&w=900&q=85'),
    ('Кашпо та вазони', 'https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=900&q=85'),
    ('Контейнери для розсади', 'https://images.unsplash.com/photo-1497250681960-ef046c08a56e?auto=format&fit=crop&w=900&q=85')
)
INSERT INTO categories(name, image_url)
SELECT s.name, s.image_url FROM seed s
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.name = s.name);
