-- Таблиця елементів кошика
-- Кожен рядок = товар у конкретному кошику

CREATE TABLE cart_items
(
    -- Унікальний ідентифікатор
    cart_item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Посилання на кошик
    cart_id      BIGINT NOT NULL,

    -- Посилання на товар
    product_id   BIGINT NOT NULL,

    -- Кількість товару
    -- Не може бути 0 або від’ємною
    quantity     BIGINT NOT NULL CHECK (quantity > 0),

    -- Забороняє дублікати одного товару в одному кошику
    CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id),

    -- FK на carts
    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id)
            REFERENCES carts (cart_id)
            ON DELETE CASCADE,

    -- FK на products
    CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id)
            REFERENCES products (product_id)
            ON DELETE RESTRICT
);

-- Індекси для швидких JOIN
CREATE INDEX idx_cart_items_cart_id ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items (product_id);