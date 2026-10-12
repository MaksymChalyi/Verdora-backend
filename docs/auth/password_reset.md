# Password Reset Flow

Цей документ описує, як у Verdora backend працюють функції "forgot password" та "reset password".

---

## Огляд

Процес відновлення пароля складається з двох кроків:

1. **Forgot Password** — користувач запитує лист зі лінком для відновлення пароля
2. **Reset Password** — користувач вводить новий пароль, використовуючи токен з листа

---

## Forgot Password

### Ендпоінт

```
POST /auth/forgot-password
```

### Тіло запиту

```json
{
  "email": "user@example.com"
}
```

### HTTP-відповіді

| Статус | Опис |
|--------|-------------|
| 200 OK | Лист для відновлення успішно надіслано |
| 404 Not Found | Користувача з таким email не існує |

### Діаграма флоу

```
Client                        Backend                        Database                    SendGrid API
  │                               │                               │                           │
  │  POST /auth/forgot-password   │                               │                           │
  │  { email }                    │                               │                           │
  │──────────────────────────────>│                               │                           │
  │                               │  findUserByEmail()            │                           │
  │                               │──────────────────────────────>│                           │
  │                               │  User found                   │                           │
  │                               │<──────────────────────────────│                           │
  │                               │                               │                           │
  │                               │  deleteAllByUserId()          │                           │
  │                               │  (анулювати старі токени)     │                           │
  │                               │──────────────────────────────>│                           │
  │                               │                               │                           │
  │                               │  Згенерувати UUID-токен        │                           │
  │                               │  expiresAt = now + 15 хв      │                           │
  │                               │                               │                           │
  │                               │  save(PasswordResetToken)     │                           │
  │                               │──────────────────────────────>│                           │
  │                               │                               │                           │
  │                               │  POST mail/send (SendGrid)    │                           │
  │                               │──────────────────────────────────────────────────────────>│
  │                               │                               │                           │
  │  200 OK                       │                               │                           │
  │<──────────────────────────────│                               │                           │
```

### Що відбувається всередині

1. Backend шукає користувача за email — якщо не знайдено, кидається `UserNotFoundException` (404)
2. Усі попередні reset-токени цього користувача видаляються з бази (`passwordResetTokenRepository.deleteAllByUserId`)
3. Генерується новий UUID-токен (`UUID.randomUUID()`) і зберігається з терміном дії 15 хвилин
4. На email користувача відправляється лист з лінком для відновлення:

```
<app.frontend-url>/reset-password?token=<uuid>
```

Лист надсилається через **SendGrid API** (`EmailServiceImpl`, бібліотека `com.sendgrid:sendgrid-java`), а не через SMTP. Ключ API береться з `app.sendgrid-api-key` (`SENDGRID_API_KEY` у `.env`), адреса відправника — з `app.mail-from` (`MAIL_FROM`).

---

## Reset Password

### Ендпоінт

```
POST /auth/reset-password
```

### Тіло запиту

```json
{
  "token": "bf8d94df-4a90-4858-8ed9-7d4cc9deed26",
  "newPassword": "NewSecurePassword123"
}
```

### HTTP-відповіді

| Статус | Опис |
|--------|-------------|
| 200 OK | Пароль успішно змінено |
| 400 Bad Request | Токен невалідний (`Invalid reset token`) |
| 400 Bad Request | Токен вже використано (`Reset token already used`) |
| 400 Bad Request | Токен протермінований (`Reset token expired`) |

### Діаграма флоу

```
Client                        Backend                        Database
  │                               │                               │
  │  POST /auth/reset-password    │                               │
  │  { token, newPassword }       │                               │
  │──────────────────────────────>│                               │
  │                               │  findByToken(token)           │
  │                               │──────────────────────────────>│
  │                               │  Token found                  │
  │                               │<──────────────────────────────│
  │                               │                               │
  │                               │  Перевірка: token.isUsed()?   │
  │                               │  → так: кинути 400            │
  │                               │                               │
  │                               │  Перевірка: expiresAt         │
  │                               │  < now? → так: кинути 400     │
  │                               │                               │
  │                               │  BCrypt encode newPassword    │
  │                               │  user.setPasswordHash(...)    │
  │                               │  userRepository.save(user)    │
  │                               │──────────────────────────────>│
  │                               │                               │
  │                               │  token.setUsed(true)          │
  │                               │  tokenRepository.save(token)  │
  │                               │──────────────────────────────>│
  │                               │                               │
  │  200 OK                       │                               │
  │<──────────────────────────────│                               │
```

### Що відбувається всередині

1. Backend шукає токен у таблиці `password_reset_tokens` (`findByToken`)
2. Якщо токен не знайдено → `InvalidResetTokenException` (400, `"Invalid reset token"`)
3. Якщо токен вже використано → `ResetTokenAlreadyUsedException` (400, `"Reset token already used"`)
4. Якщо токен протермінований (старший за 15 хвилин) → `ResetTokenExpiredException` (400, `"Reset token expired"`)
5. Новий пароль кодується BCrypt і зберігається у користувача
6. Токен позначається як `used = true`, щоб його неможливо було використати повторно

---

## Таблиця БД

```sql
CREATE TABLE password_reset_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255) NOT NULL UNIQUE,
    user_id    BIGINT       NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

(Міграція `V10__create_password_reset_tokens_table.sql`.)

---

## Конфігурація

Лінк для відновлення, що надсилається листом, формується з властивості `app.frontend-url` у `application.yaml`:

```yaml
app:
  frontend-url: ${FRONTEND_URL:http://localhost:5173}
```

| Середовище | Значення |
|-------------|-------|
| Локальне | `http://localhost:5173` |
| Продакшн | URL фронтенду, заданий через `FRONTEND_URL` |

Email надсилається через **SendGrid API**, облікові дані — у змінних середовища:

```
MAIL_FROM=your@email.com
SENDGRID_API_KEY=your-sendgrid-api-key
```

---

## Міркування щодо безпеки

- Токени протерміновуються через **15 хвилин**
- Кожен новий запит forgot-password **анулює всі попередні токени** цього користувача
- Токени — **одноразові**: позначаються `used = true` після успішного відновлення
- Паролі хешуються через **BCrypt** перед збереженням
- Токен — **випадковий UUID**, непередбачуваний
