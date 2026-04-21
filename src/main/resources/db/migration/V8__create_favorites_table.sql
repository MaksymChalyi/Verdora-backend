-- Таблиця обраних товарів (favorites)
-- Зв’язок many-to-many між users і products

CREATE TABLE favorites
(
    -- Користувач
    user_id    BIGINT                      NOT NULL,

    -- Товар
    product_id BIGINT                      NOT NULL,

    -- Дата додавання в обране
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Композитний первинний ключ
    -- Забороняє дублікати
    PRIMARY KEY (user_id, product_id),

    -- FK на users
    CONSTRAINT fk_favorites_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE,

    -- FK на products
    CONSTRAINT fk_favorites_product
        FOREIGN KEY (product_id)
            REFERENCES products (product_id)
            ON DELETE CASCADE
);

-- Індекси для швидкого пошуку
CREATE INDEX idx_favorites_user_id ON favorites (user_id);
CREATE INDEX idx_favorites_product_id ON favorites (product_id);