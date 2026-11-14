-- Опис товару стає обов'язковим.
-- Спочатку заповнюємо порожні/відсутні описи назвою товару, щоб не зламати NOT NULL.
UPDATE products
SET description = name
WHERE description IS NULL
   OR TRIM(description) = '';

ALTER TABLE products
    ALTER COLUMN description SET NOT NULL;
