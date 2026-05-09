INSERT INTO users (name, email, password_hash, role, created_at, updated_at)
VALUES ('Admin',
        'admin@verdora.com',
        '$2a$10$2Y8OeOw8hHSbi.Oihi43du7ie0E5OtKgBOMlVmSNObfn42B2FhkR.',
        'ADMIN',
        NOW(),
        NOW());

-- Креди для входу:
-- email: admin@verdora.com
-- password: password