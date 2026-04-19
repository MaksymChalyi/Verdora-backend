-- Таблиця категорій товарів
-- Кожна категорія має унікальний ID і назву

CREATE TABLE categories
(
    -- GENERATED ALWAYS AS IDENTITY
    -- PostgreSQL сам генерує унікальний ID
    -- Це краще ніж вручну задавати значення
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Назва категорії
    -- NOT NULL, бо категорія без назви не має сенсу
    name VARCHAR(256) NOT NULL
);