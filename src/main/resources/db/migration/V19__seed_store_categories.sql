-- VERDORA SHOWCASE DATA (synthetic). PostgreSQL / Flyway.
-- Production Flyway migration: applied by default from src/main/resources/db/migration.
-- Do not use real personal information in showcase data.

-- 15 store categories. Never overwrite existing categories.
WITH seed(name, image_url) AS (
    VALUES
    ('Смартфони', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=1000&q=85'),
    ('Ноутбуки', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=1000&q=85'),
    ('Монітори', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=1000&q=85'),
    ('Навушники', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=1000&q=85'),
    ('Клавіатури', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=1000&q=85'),
    ('Комп’ютерні миші', 'https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=1000&q=85'),
    ('Смарт-годинники', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=1000&q=85'),
    ('Фотоапарати', 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=1000&q=85'),
    ('Повербанки', 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=1000&q=85'),
    ('Розумний дім', 'https://images.unsplash.com/photo-1558002038-1055907df827?auto=format&fit=crop&w=1000&q=85'),
    ('Побутова техніка', 'https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=1000&q=85'),
    ('Офісні крісла', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=1000&q=85'),
    ('Зарядні пристрої', 'https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&w=1000&q=85'),
    ('Планшети', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&w=1000&q=85'),
    ('Акустичні системи', 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=1000&q=85')
)
INSERT INTO categories (name, image_url)
SELECT s.name, s.image_url
FROM seed s
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.name = s.name);
