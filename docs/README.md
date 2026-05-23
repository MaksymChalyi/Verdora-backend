# Verdora Backend

Backend service for the Verdora e-commerce platform, built with Spring Boot. Handles authentication (email/password and Google OAuth2), user management, product catalog, shopping cart, orders, and favorites.

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
| Docker | Containerization |
| GitHub Actions | CI/CD |

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

ALLOWED_ORIGIN_1=http://localhost:5173
ALLOWED_ORIGIN_2=http://localhost:8081

MAIL_FROM=your_email
SENDGRID_API_KEY=your_sendgrid_key
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
  "timestamp": "2026-05-23T13:55:49.772Z",
  "status": 200,
  "message": "Success",
  "data": {}
}
```

On error:

```json
{
  "timestamp": "2026-05-23T13:55:49.773Z",
  "status": 400,
  "message": "Error message",
  "data": null
}
```

### Endpoints

#### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/sign-up` | ❌ | Register a new user |
| POST | `/auth/sign-in` | ❌ | Login with email and password |
| POST | `/auth/refresh` | ❌ (cookie) | Refresh access token |
| POST | `/auth/logout` | ✅ | Clear auth cookies |
| POST | `/auth/forgot-password` | ❌ | Send password reset email |
| POST | `/auth/reset-password` | ❌ | Reset password with token |
| GET | `/oauth2/authorization/google` | ❌ | Google OAuth2 login |

#### Users

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/users/me` | ✅ | Get current user |
| PUT | `/users/me` | ✅ | Update current user |
| GET | `/users` | ✅ ADMIN | Get all users (paginated) |
| DELETE | `/users/{id}` | ✅ ADMIN | Delete user |

#### Products

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/products` | ❌ | Get products (with filters) |
| GET | `/products/{id}` | ❌ | Get product by ID |
| POST | `/products` | ✅ ADMIN | Create product |
| PUT | `/products/{id}` | ✅ ADMIN | Update product |
| DELETE | `/products/{id}` | ✅ ADMIN | Delete product |

**Filter params for `GET /products`:**

| Param | Type | Description |
|---|---|---|
| `categoryId` | Long | Filter by category |
| `minPrice` | BigDecimal | Minimum price |
| `maxPrice` | BigDecimal | Maximum price |
| `discount` | Boolean | Only discounted products |
| `page`, `size`, `sort` | — | Pagination |

#### Categories

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/categories` | ❌ | Get all categories |
| POST | `/categories` | ✅ ADMIN | Create category |
| PUT | `/categories/{id}` | ✅ ADMIN | Update category |
| DELETE | `/categories/{id}` | ✅ ADMIN | Delete category |

#### Cart

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/cart` | ✅ | Get current user's cart |
| POST | `/cart/items` | ✅ | Add item to cart |
| PUT | `/cart/items/{cartItemId}` | ✅ | Update item quantity |
| DELETE | `/cart/items/{cartItemId}` | ✅ | Remove item from cart |
| DELETE | `/cart` | ✅ | Clear cart |

#### Orders

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/orders` | ✅ | Place order from cart |
| GET | `/orders` | ✅ | Get all user's orders |
| GET | `/orders/{orderId}` | ✅ | Get order by ID |
| DELETE | `/orders/{orderId}` | ✅ | Cancel order (PENDING only) |
| PATCH | `/orders/{orderId}/status` | ✅ ADMIN | Update order status |

**Order statuses:** `PENDING` → `PAID` → `SHIPPED` → `CANCELLED`

#### Favorites

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/favorites` | ✅ | Get favorite products |
| POST | `/favorites/{productId}` | ✅ | Add to favorites |
| DELETE | `/favorites/{productId}` | ✅ | Remove from favorites |

#### Health

| Method | Endpoint | Auth | Description |
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
└── config/           # OpenAPI, security beans, app properties
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
├── V8__create_favorites_table.sql
├── V9__insert_default_admin.sql
└── V10__create_password_reset_tokens_table.sql
```

Default admin credentials (from V9 migration):
- **Email:** `admin@verdora.com`
- **Password:** `password`

---

## Swagger

- UI: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- OpenAPI spec: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

---

## Docker

```bash
docker-compose up -d
```

See [Docker docs](./docs/docker/docker.md) for details.

---

## Documentation

- [Sign In](./auth/sign-in.md)
- [Sign Up](./auth/sign-up.md)
- [JWT](./auth/jwt.md)
- [Google OAuth](./auth/google-oauth.md)
- [Password Reset](./auth/password_reset.md)
- [Cart](./cart/cart.md)
- [Orders](./orders/orders.md)
- [Favorites](./favorites/favorites.md)
- [Docker](./docker/docker.md)
- [CI/CD](./ci-cd/github-ci.md)
