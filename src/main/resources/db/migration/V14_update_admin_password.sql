-- Updates password for the default admin account.
--
-- Credentials:
-- email: admin@verdora.com
-- password: Password1!
--
-- Admin account was created in the original admin seed migration.

UPDATE users
SET password_hash = '$2a$10$KPoH51KvI/b2pAsnjF4.1.0PXJt/TKLF9ZnCjxO7po/DR97AMqxpO',
    updated_at = NOW()
WHERE email = 'admin@verdora.com';