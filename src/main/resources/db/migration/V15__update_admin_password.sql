UPDATE users
SET password_hash = '$2a$10$lX3vBuJVt5pu4y9z5HDoN.XkhZoa7Q53dJ3QeB3kV/70yKGGC8fDe',
    updated_at = NOW()
WHERE email = 'admin@verdora.com';