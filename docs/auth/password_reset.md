# Password Reset Flow

This document describes how the forgot password and reset password features work in the Verdora backend.

---

## Overview

The password reset flow consists of two steps:

1. **Forgot Password** — the user requests a password reset link via email
2. **Reset Password** — the user submits a new password using the token from the email

---

## Forgot Password

### Endpoint

```
POST /auth/forgot-password
```

### Request Body

```json
{
  "email": "user@example.com"
}
```

### HTTP Responses

| Status | Description |
|--------|-------------|
| 200 OK | Reset email sent successfully |
| 404 Not Found | User with this email does not exist |

### Flow Diagram

```
Client                        Backend                        Database                    Gmail SMTP
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
  │                               │  (invalidate old tokens)      │                           │
  │                               │──────────────────────────────>│                           │
  │                               │                               │                           │
  │                               │  Generate UUID token          │                           │
  │                               │  Set expiresAt = now + 15min  │                           │
  │                               │                               │                           │
  │                               │  save(PasswordResetToken)     │                           │
  │                               │──────────────────────────────>│                           │
  │                               │                               │                           │
  │                               │  sendPasswordResetEmail()     │                           │
  │                               │──────────────────────────────────────────────────────────>│
  │                               │                               │                           │
  │  200 OK                       │                               │                           │
  │<──────────────────────────────│                               │                           │
```

### What Happens Internally

1. Backend looks up the user by email — throws `UserNotFoundException` (404) if not found
2. All previous reset tokens for this user are deleted from the database
3. A new UUID token is generated and saved with a 15-minute expiry
4. An email is sent to the user containing a reset link:

```
https://<frontend-url>/reset-password?token=<uuid>
```

---

## Reset Password

### Endpoint

```
POST /auth/reset-password
```

### Request Body

```json
{
  "token": "bf8d94df-4a90-4858-8ed9-7d4cc9deed26",
  "newPassword": "NewSecurePassword123"
}
```

### HTTP Responses

| Status | Description |
|--------|-------------|
| 200 OK | Password reset successfully |
| 400 Bad Request | Token is invalid |
| 400 Bad Request | Token has already been used |
| 400 Bad Request | Token has expired (older than 15 minutes) |

### Flow Diagram

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
  │                               │  Check: token.isUsed()?       │
  │                               │  → yes: throw 400             │
  │                               │                               │
  │                               │  Check: token.expiresAt       │
  │                               │  < now? → yes: throw 400      │
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

### What Happens Internally

1. Backend looks up the token in `password_reset_tokens` table
2. If token is not found → `InvalidResetTokenException` (400)
3. If token is already used → `ResetTokenAlreadyUsedException` (400)
4. If token is expired (older than 15 minutes) → `ResetTokenExpiredException` (400)
5. New password is encoded with BCrypt and saved to the user
6. Token is marked as `used = true` so it cannot be reused

---

## Database Table

```sql
CREATE TABLE password_reset_tokens (
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255) NOT NULL UNIQUE,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    used       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

## Configuration

The reset link sent in the email uses the `app.frontend-url` property from `application.yaml`:

```yaml
app:
  frontend-url: ${FRONTEND_URL:http://localhost:5173}
```

| Environment | Value |
|-------------|-------|
| Local | `http://localhost:5173` |
| Production | `https://irfi4.github.io/Front-end-Verdora` |

Email is sent via Gmail SMTP using credentials stored in environment variables:

```
GMAIL_USERNAME=your@gmail.com
GMAIL_PASSWORD=your-app-password
MAIL_FROM=your@gmail.com
```

---

## Security Considerations

- Tokens expire after **15 minutes**
- Each new forgot-password request **invalidates all previous tokens** for that user
- Tokens are **single-use** — marked as `used = true` after successful reset
- Passwords are hashed with **BCrypt** before storage
- Token is a **random UUID** — not guessable
