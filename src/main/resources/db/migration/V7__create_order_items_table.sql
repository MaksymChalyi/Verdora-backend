-- Таблиця товарів у замовленні
-- Кожен рядок = товар у конкретному замовленні

CREATE TABLE order_items
(
    -- Унікальний ідентифікатор
    order_item_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Посилання на замовлення
    order_id          BIGINT            NOT NULL,

    -- Посилання на товар
    product_id        BIGINT            NOT NULL,

    -- Кількість товару
    quantity          INT            NOT NULL CHECK (quantity > 0),

    -- Ціна на момент покупки
    -- Обов'язкове поле
    price_at_purchase DECIMAL(10, 2) NOT NULL,

    -- Забороняє дублікати одного товару в одному замовленні
    CONSTRAINT uq_order_product UNIQUE (order_id, product_id),

    -- FK на orders
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders (order_id)
            ON DELETE CASCADE,

    -- FK на products
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id)
            REFERENCES products (product_id)
            ON DELETE RESTRICT
);

-- Індекси для JOIN
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);