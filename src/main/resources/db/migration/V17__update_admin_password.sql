UPDATE users
SET password_hash = '$2a$10$8FcB3g.aGu1yZtq8L//pzesNqT2QmC0Bglb0EYOZS1WcAq6CGXZAG',
    updated_at = NOW()
WHERE email = 'admin@verdora.com';