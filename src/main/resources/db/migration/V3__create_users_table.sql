-- Таблиця користувачів
-- Базова сутність, від якої залежить більшість інших таблиць

CREATE TABLE users
(
    -- Унікальний ідентифікатор
    -- Генерується автоматично PostgreSQL
    user_id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Ім'я користувача
    name          VARCHAR(256)             NOT NULL,

    -- Email
    -- UNIQUE гарантує відсутність дублювання
    -- NOT NULL, бо використовується для логіну
    email         VARCHAR(256)             NOT NULL UNIQUE,

    -- Телефон
    -- Не обов'язковий, бо не всі користувачі його мають
    phone_number  VARCHAR(32),

    -- Хеш пароля
    -- Ніколи не зберігати пароль у відкритому вигляді
    password_hash VARCHAR(256)             NOT NULL,

    -- Роль користувача
    -- Обмеження через CHECK
    role          VARCHAR(32)              NOT NULL
        CHECK (role IN ('USER', 'ADMIN')),

    -- Дата створення
    -- Автоматично виставляється при INSERT
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Дата останнього оновлення
    -- Має оновлюватись при UPDATE (через код)
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL
);