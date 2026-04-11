-- Таблиця товарів
-- Кожен товар належить до однієї категорії (зв'язок many-to-one)

CREATE TABLE products
(
    -- Автоінкрементний первинний ключ
    product_id     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Назва товару
    -- Обов'язкове поле
    name           VARCHAR(256)             NOT NULL,

    -- Опис товару
    -- TEXT без обмеження довжини
    description    TEXT,

    -- Ціна товару
    -- DECIMAL(10,2) означає:
    -- максимум 10 цифр, 2 після коми
    -- стандарт для грошей
    price          DECIMAL(10, 2)           NOT NULL,

    -- Посилання на категорію
    -- NOT NULL, бо товар має належати категорії
    category_id    INT                      NOT NULL,

    -- URL зображення
    -- 512 символів, бо URL часто довгі
    image_url      VARCHAR(512),

    -- Ціна зі знижкою
    -- NULL означає, що знижки немає
    discount_price DECIMAL(10, 2),

    -- Дата створення
    -- CURRENT_TIMESTAMP автоматично підставляє поточний час
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Дата останнього оновлення
    -- Початково така ж як created_at
    -- Далі має оновлюватись при UPDATE (через код або тригер)
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Зовнішній ключ
    -- Забезпечує, що category_id існує в таблиці categories
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
            REFERENCES categories (category_id)
            ON DELETE RESTRICT -- не дає видалити категорію, якщо є товари
            ON UPDATE CASCADE  -- якщо зміниться ID категорії, оновиться і тут
);

-- Індекс для швидкого пошуку по category_id
-- Критично для JOIN і WHERE
CREATE INDEX idx_products_category_id ON products (category_id);