-- Таблиця кошиків
-- 1 користувач = 1 кошик (жорстко обмежено через UNIQUE)

CREATE TABLE carts
(
    -- Унікальний ідентифікатор кошика
    cart_id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Посилання на користувача
    -- UNIQUE гарантує, що у користувача тільки один кошик
    user_id    INT                      NOT NULL UNIQUE,

    -- Дата створення кошика
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Дата останнього оновлення
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Зовнішній ключ на users
    CONSTRAINT fk_carts_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE -- якщо видалили користувача, видаляється і кошик
            ON UPDATE CASCADE
);

-- Індекс створюється автоматично через UNIQUE,
-- додатковий індекс не потрібен