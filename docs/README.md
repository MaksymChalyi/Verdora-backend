# Verdora Backend

Backend service for the Verdora e-commerce platform, built with Spring Boot. Handles authentication (email/password and Google OAuth2), user management, and product categories.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Language |
| Spring Boot 3 | Framework |
| Spring Security | Authentication & authorization |
| Spring Data JPA | Database access |
| PostgreSQL | Database |
| Flyway | Database migrations |
| MapStruct | DTO mapping |
| springdoc (Swagger) | API documentation |
| Lombok | Boilerplate reduction |

---

## Getting Started

### Prerequisites

- Java 21
- Maven
- PostgreSQL

### 1. Create the database

```sql
CREATE DATABASE verdora_db;
```

### 2. Configure environment

Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
```

```env
SERVER_PORT=8081

DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://localhost:5432/verdora_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
REDIRECT_URL=http://localhost:5173

JWT_SECRET=your_jwt_secret
```

### 3. Run

```bash
./mvnw spring-boot:run
```

### 4. Verify

```
GET /health/ping
```

Expected response:
```json
{
  "status": 200,
  "message": "Service is alive",
  "data": "pong"
}
```

---

## API

### Base URL

```
http://localhost:8081
```

### Response format

All endpoints return a unified wrapper:

```json
{
  "timestamp": "2026-04-21T13:55:49.772Z",
  "status": 200,
  "message": "Success",
  "data": {}
}
```

On error:

```json
{
  "timestamp": "2026-04-21T13:55:49.773Z",
  "status": 400,
  "message": "Error message",
  "data": null
}
```

### Endpoints

#### Auth

| Method | Endpoint | Auth required | Description |
|---|---|---|---|
| POST | `/auth/register` | ❌ | Register a new user |
| POST | `/auth/login` | ❌ | Login with email and password |
| POST | `/auth/refresh` | ❌ (cookie) | Refresh access token |
| POST | `/auth/logout` | ❌ | Clear auth cookies |

#### Users

| Method | Endpoint | Auth required | Description |
|---|---|---|---|
| GET | `/users/current-user` | ✅ | Get current authenticated user email |

#### [Categories](./categories/categories.md)

| Method | Endpoint | Auth required | Description |
|---|---|---|---|
| POST | `/categories` | ❌ | Create a category |
| PUT | `/categories/{id}` | ❌ | Update a category |
| DELETE | `/categories/{id}` | ❌ | Delete a category |

#### Health

| Method | Endpoint | Auth required | Description |
|---|---|---|---|
| GET | `/health/ping` | ❌ | Service availability check |

---

## Authentication

Cookie-based authentication using JWT. After login or registration two `HttpOnly` cookies are set:

| Cookie | Path | Expiration |
|---|---|---|
| `accessToken` | `/` | 15 minutes |
| `refreshToken` | `/auth/refresh` | 7 days |

Cookies are sent automatically by the browser on every request. `HttpOnly` prevents JavaScript from reading them (XSS protection).

When `accessToken` expires — call `POST /auth/refresh`. The `refreshToken` cookie is sent automatically and a new `accessToken` is issued.

---

## Project Structure

```
src/main/java/com/verdorabackend/
├── controller/       # REST endpoints
├── service/          # Business logic
│   └── impl/
├── repository/       # JPA repositories
├── entity/           # JPA entities
├── dto/
│   ├── request/      # Incoming payloads
│   ├── response/     # Outgoing payloads
│   └── auth/         # Internal auth results
├── security/         # JWT, OAuth2, filters, cookies
├── exception/        # Custom exceptions
│   └── handler/      # GlobalExceptionHandler
├── mapper/           # MapStruct mappers
└── config/           # OpenAPI config
```

---

## Database

Migrations are managed by Flyway and run automatically on startup.

```
resources/db/migration/
├── V1__create_categories_table.sql
├── V2__create_products_table.sql
├── V3__create_users_table.sql
├── V4__create_carts_table.sql
├── V5__create_cart_items_table.sql
├── V6__create_orders_table.sql
├── V7__create_order_items_table.sql
└── V8__create_favorites_table.sql
```

---

## Swagger

- UI: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- OpenAPI spec: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

---

## Documentation

- [Sign In](./auth/sign-in.md)
- [Sign Up](./auth/sign-up.md)
- [Google OAuth](./auth/google-oauth.md)
- [Docker](./docker/docker.md)
