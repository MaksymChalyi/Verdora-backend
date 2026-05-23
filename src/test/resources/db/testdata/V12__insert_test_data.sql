-- =============================================
-- Тестові дані для інтеграційних тестів
-- Кладеться в src/test/resources/db/testdata/
-- НЕ потрапляє на продакшн
-- =============================================

-- Категорії
INSERT INTO categories (name)
VALUES ('Електроніка'),
       ('Одяг'),
       ('Книги');

-- Продукти (3 штуки достатньо для тестів)
-- product_id=1: має знижку (discountPrice < price)
-- product_id=2: без знижки
-- product_id=3: категорія 2
INSERT INTO products (name, description, price, category_id, image_url, discount_price, created_at, updated_at)
VALUES ('Ноутбук Test', 'Тестовий ноутбук для інтеграційних тестів', 1000.00, 1, 'https://example.com/laptop.jpg',
        800.00, NOW(), NOW()),
       ('Смартфон Test', 'Тестовий смартфон для інтеграційних тестів', 500.00, 1, 'https://example.com/phone.jpg',
        500.00, NOW(), NOW()),
       ('Футболка Test', 'Тестова футболка для інтеграційних тестів', 100.00, 2, 'https://example.com/tshirt.jpg',
        80.00, NOW(), NOW());