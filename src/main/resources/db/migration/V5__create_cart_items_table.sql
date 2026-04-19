-- Таблиця замовлень
-- Кожен запис = одне оформлене замовлення

CREATE TABLE orders
(
    -- Унікальний ідентифікатор
    order_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Користувач, який зробив замовлення
    user_id INT NOT NULL,

    -- Загальна сума замовлення
    -- Фіксується на момент покупки
    total_price DECIMAL(10, 2) NOT NULL,

    -- Статус замовлення
    -- Контроль значень через CHECK
    status VARCHAR(32) NOT NULL
        CHECK (status IN ('PENDING', 'PAID', 'SHIPPED', 'CANCELLED')),

    -- Дата створення
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- FK на users
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE RESTRICT
);

-- Індекс для швидкого пошуку замовлень користувача
CREATE INDEX idx_orders_user_id ON orders(user_id);