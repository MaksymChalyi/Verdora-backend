-- =============================================
-- Тестові юзери для інтеграційних тестів
-- Кладеться в src/test/resources/db/testdata/
-- НЕ потрапляє на продакшн
-- password для всіх: password
-- =============================================

INSERT INTO users (name, email, password_hash, role, created_at, updated_at)
VALUES (
           'Ivan Test',
           'ivan@verdora.com',
           '$2a$10$2Y8OeOw8hHSbi.Oihi43du7ie0E5OtKgBOMlVmSNObfn42B2FhkR.',
           'USER',
           NOW(), NOW()
       );