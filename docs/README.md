# Verdora Backend

Backend-сервіс для e-commerce платформи Verdora, побудований на Spring Boot. Відповідає за автентифікацію (email/пароль та Google OAuth2), керування користувачами, каталог товарів, кошик, замовлення, вибране та адмінські звіти.

---

## Технологічний стек

| Технологія | Призначення |
|---|---|
| Java 21 | Мова програмування |
| Spring Boot 3.3 | Фреймворк |
| Spring Security | Автентифікація та авторизація |
| Spring Data JPA | Доступ до бази даних |
| PostgreSQL | База даних |
| Flyway | Міграції бази даних |
| MapStruct | Мапінг DTO |
| springdoc (Swagger) | Документація API |
| Lombok | Скорочення boilerplate-коду |
| Cloudinary | Зберігання зображень товарів |
| SendGrid | Відправка email (відновлення пароля) |

---

## Початок роботи

### Передумови

- Java 21
- Maven
- PostgreSQL

### 1. Створення бази даних

```sql
CREATE DATABASE verdora_db;
```

### 2. Налаштування середовища

Скопіюйте `.env.example` у `.env` і заповніть власними значеннями (`.env` підхоплюється автоматично через `spring-dotenv`):

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

FRONTEND_URL=http://localhost:5173
ALLOWED_ORIGIN_1=http://localhost:5173
ALLOWED_ORIGIN_2=http://localhost:8081
ALLOWED_ORIGIN_3=

MAIL_FROM=your_email
SENDGRID_API_KEY=your_sendgrid_key

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_key
CLOUDINARY_API_SECRET=your_cloudinary_secret
```

| Змінна | Обов'язкова | Default в `application.yaml` | Призначення |
|---|---|---|---|
| `SERVER_PORT` | ❌ | `8081` | Порт сервера |
| `DB_DRIVER`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | ✅ | — | Підключення до PostgreSQL |
| `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` | ✅ | — | Google OAuth2 |
| `REDIRECT_URL` | ❌ | `http://localhost:5173` | Куди редиректити після Google-логіну |
| `JWT_SECRET` | ❌ (але обов'язково перевизначити в продакшені) | dev-значення | Ключ підпису JWT (мін. 32 байти) |
| `FRONTEND_URL` | ❌ | `http://localhost:5173` | Базовий URL для посилання у листі скидання пароля |
| `ALLOWED_ORIGIN_1..3` | ❌ | `:5173`, `:8081`, порожньо | CORS-origins |
| `MAIL_FROM`, `SENDGRID_API_KEY` | ✅ | — | Відправка листів через SendGrid |
| `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` | ✅ | — | Завантаження зображень товарів |

> `.env.example` наразі не синхронізований з `application.yaml`: у ньому є `GMAIL_USERNAME`/`GMAIL_PASSWORD` (не використовуються — пошта йде через SendGrid) і бракує `SENDGRID_API_KEY` та `ALLOWED_ORIGIN_3`. Орієнтуйтеся на таблицю вище.

### 3. Запуск

```bash
./mvnw spring-boot:run
```

### 4. Перевірка

```
GET /health/ping
```

Очікувана відповідь:
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

### Формат відповіді

Усі ендпоінти повертають уніфіковану обгортку (`BaseResponse`):

```json
{
  "timestamp": "2026-05-23T13:55:49.772Z",
  "status": 200,
  "message": "Success",
  "data": {}
}
```

У разі помилки:

```json
{
  "timestamp": "2026-05-23T13:55:49.773Z",
  "status": 400,
  "message": "Error message",
  "data": null
}
```

### Коди помилок

Помилки обробляє `GlobalExceptionHandler` (кастомні винятки наслідують `BaseException` і несуть свій HTTP-статус).

| Код | Коли |
|---|---|
| 400 | Помилка Bean Validation (повертається повідомлення **першої** помилки поля), невалідний JSON (`"Malformed JSON request"`), невалідний тип параметра (`"Parameter 'x' must be a valid Long"`), порожній кошик при оформленні, невалідний/використаний/протермінований reset-токен, email уже зареєстровано, невалідні параметри звітів |
| 401 | Немає/невалідний `accessToken` на захищеному ендпоінті (`{"status":401,"message":"Unauthorized","data":null}` — без `timestamp`), невірні облікові дані, невалідний refresh-токен |
| 403 | Роль не `ADMIN` для адмінського ендпоінту (`"Forbidden"`) |
| 404 | Ресурс не знайдено або належить іншому користувачу (кошик, замовлення) |
| 409 | Конфлікт: дубль категорії/вибраного, видалення категорії/товару, що використовуються, недопустимий перехід статусу замовлення |
| 500 | Необроблена помилка (`"Internal server error"`), помилка завантаження в Cloudinary |

### Пагінація і сортування

Списки повертають стандартну Spring Data `Page` (`content`, `number`, `size`, `totalElements`, `totalPages`, `first`, `last`, …) у полі `data`. Параметри: `page` (з 0), `size`, `sort=field,asc|desc`.

| Ендпоінт | Default `size` | Макс. `size` | Default сортування |
|---|---|---|---|
| `GET /products` | 12 | не обмежено | `id` ASC |
| `GET /categories` | 12 | не обмежено | `id` ASC |
| `GET /users` | 12 | не обмежено | `id` ASC |
| `GET /orders` | 12 | **12** (більше обрізається) | `createdAt` DESC |
| `GET /orders/all` | 12 | не обмежено | `createdAt` DESC |
| `GET /favorites` | 12 | **12** (більше обрізається) | `createdAt` DESC |

### Ендпоінти

#### Auth

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| POST | `/auth/register` | ❌ | Реєстрація нового користувача |
| POST | `/auth/login` | ❌ | Логін за email і паролем |
| POST | `/auth/refresh` | ❌ (cookie `refreshToken`) | Оновити `accessToken` |
| POST | `/auth/logout` | ❌ (очищає cookies, якщо вони є) | Вийти, очистити auth-cookies |
| POST | `/auth/forgot-password` | ❌ | Надіслати лист для відновлення пароля |
| POST | `/auth/reset-password` | ❌ | Встановити новий пароль за токеном |
| GET | `/oauth2/authorization/google` | ❌ | Початок Google OAuth2 логіну |

#### Users

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| GET | `/users/current-user` | ✅ | Профіль поточного користувача |
| PUT | `/users/current-user` | ✅ | Оновити ім'я/телефон поточного користувача |
| DELETE | `/users/current-user` | ✅ (+ пароль у тілі) | Видалити власний акаунт |
| GET | `/users` | ✅ ADMIN | Сторінка всіх користувачів |

#### Products

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| GET | `/products` | ❌ | Сторінка товарів з фільтрами/пошуком; якщо користувач авторизований — враховує `isFavorite` |
| GET | `/products/{id}` | ❌ | Товар за ID (з `isFavorite` для авторизованого користувача) |
| GET | `/products/product-of-the-day` | ❌ | Найбільш продаваний товар (за кількістю в замовленнях `PAID`/`SHIPPED`/`DELIVERED`); 200 з `data: null`, якщо продажів немає |
| POST | `/products` | ✅ ADMIN | Створити товар |
| PUT | `/products/{id}` | ✅ ADMIN | Оновити товар |
| DELETE | `/products/{id}` | ✅ ADMIN | Видалити товар (409, якщо він у кошику чи замовленні) |
| POST | `/products/upload-image` | ✅ ADMIN | Завантажити зображення товару (Cloudinary, multipart, JPEG/PNG/WEBP, до 5 MB) |

**Параметри фільтрації `GET /products`:**

| Параметр | Тип | Опис |
|---|---|---|
| `categoryId` | Long | Фільтр за категорією |
| `minPrice` | BigDecimal, `>= 0` | Мінімальна **ефективна** ціна |
| `maxPrice` | BigDecimal, `>= 0` | Максимальна **ефективна** ціна |
| `discount` | Boolean | `true` — лише товари з діючою знижкою; `false` — лише без знижки |
| `search` | String | Пошук за назвою (регістронезалежний) |
| `page`, `size`, `sort` | — | Пагінація, default `size=12`, сортування `id`; `sort=price,asc\|desc` сортує за ефективною ціною |

Ефективна ціна = `discountPrice`, якщо вона задана, `> 0` і менша за `price`, інакше `price`. Детальніше — у [Products](./products/products.md).

#### Categories

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| GET | `/categories` | ❌ | Сторінка категорій |
| GET | `/categories/{id}` | ❌ | Категорія за ID |
| POST | `/categories` | ✅ ADMIN | Створити категорію |
| PUT | `/categories/{id}` | ✅ ADMIN | Оновити категорію |
| DELETE | `/categories/{id}` | ✅ ADMIN | Видалити категорію |

#### Cart

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| GET | `/cart` | ✅ | Кошик поточного користувача (створюється автоматично) |
| POST | `/cart/items` | ✅ | Додати товар у кошик |
| PUT | `/cart/items/{cartItemId}` | ✅ | Змінити кількість товару |
| DELETE | `/cart/items/{cartItemId}` | ✅ | Видалити товар з кошика |
| DELETE | `/cart` | ✅ | Очистити кошик |

#### Orders

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| POST | `/orders` | ✅ | Оформити замовлення з кошика |
| GET | `/orders` | ✅ | Сторінка власних замовлень |
| GET | `/orders/{orderId}` | ✅ | Деталі свого замовлення |
| GET | `/orders/{orderId}/status` | ✅ | Легкий статус замовлення (для polling) |
| DELETE | `/orders/{orderId}` | ✅ | Скасувати своє замовлення (`PENDING`/`PAID`) |
| GET | `/orders/all` | ✅ ADMIN | Сторінка всіх замовлень з фільтрами (`status`, `dateFrom`, `dateTo`) |
| GET | `/orders/{orderId}/details` | ✅ ADMIN | Повні деталі будь-якого замовлення |
| PATCH | `/orders/{orderId}/status` | ✅ ADMIN | Змінити статус замовлення |

**Статуси замовлення:** `PENDING` → `PAID` → `SHIPPED` → `DELIVERED`, з `PENDING`/`PAID` можливий перехід у `CANCELLED`. `DELIVERED` і `CANCELLED` — фінальні. Недопустимий перехід через `PATCH` → `409`, скасування не `PENDING`/`PAID` замовлення через `DELETE` → `409`.

> Кошик і замовлення доступні лише автентифікованим користувачам — гостьового кошика та гостьового оформлення немає. Інтеграції з оплатою та складського обліку також немає: статус `PAID` виставляє адмін вручну.

#### Favorites

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| GET | `/favorites` | ✅ | Сторінка вибраних товарів |
| GET | `/favorites/{productId}` | ✅ | Чи товар у вибраному (`true`/`false`) |
| POST | `/favorites/{productId}` | ✅ | Додати товар у вибране |
| DELETE | `/favorites/{productId}` | ✅ | Видалити товар з вибраного |

#### Reports (ADMIN)

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| GET | `/reports/top-purchased` | ✅ ADMIN | До 10 товарів з найбільшою купленою кількістю |
| GET | `/reports/top-cancelled` | ✅ ADMIN | До 10 товарів з найбільшою скасованою кількістю |
| GET | `/reports/pending-payment?n={days}` | ✅ ADMIN | Замовлення в `PENDING` довше за `n` днів |
| GET | `/reports/revenue?dateFrom=&dateTo=&groupBy=` | ✅ ADMIN | Виручка, згрупована по `HOUR`, `DAY`, `WEEK` або `MONTH` |

#### Health

| Метод | Ендпоінт | Авторизація | Опис |
|---|---|---|---|
| GET | `/health/ping` | ❌ | Перевірка доступності сервісу |

---

## Автентифікація

Cookie-based автентифікація на основі JWT. Після логіну чи реєстрації встановлюються два `HttpOnly` cookies:

| Cookie | Path | Max-Age cookie / термін дії токена |
|---|---|---|
| `accessToken` | `/` | **15 хвилин** (і cookie, і JWT; обидва беруться з `app.jwt.access-expiration-ms`) |
| `refreshToken` | `/auth/refresh` | 7 днів (і cookie, і токен; `app.jwt.refresh-expiration-ms`) |

Обидва cookies — `HttpOnly`, `Secure`, `SameSite=None` (`CookieService`). Браузер надсилає їх автоматично на кожен запит у межах їхнього `path`. Ті самі cookies встановлюються після Google OAuth2-логіну.

Коли `accessToken` протермінований, захищений ендпоінт повертає `401`, і фронтенд має сам викликати `POST /auth/refresh`. `refreshToken` надсилається автоматично (cookie з path `/auth/refresh`), і видається новий `accessToken`. Автоматичного оновлення токена всередині звичайного запиту немає; refresh-токен не ротується.

**Публічні маршрути** (`SecurityConfig`): `/auth/**`, `/health/**`, `/oauth2/**`, `/login/oauth2/**`, Swagger (`/v3/api-docs/**`, `/swagger-ui/**`), `GET /products/**`, `GET /categories/**`, `OPTIONS /**`. Усе інше потребує автентифікації; адмінські ендпоінти додатково перевіряються через `@PreAuthorize("hasRole('ADMIN')")`. CSRF вимкнено.

Детальніше — у [JWT](./auth/jwt.md).

---

## Структура проєкту

```
src/main/java/com/verdorabackend/
├── controller/       # REST-ендпоінти
├── service/          # Бізнес-логіка
│   └── impl/
├── repository/       # JPA-репозиторії
│   └── specification/
├── entity/           # JPA-сутності
├── dto/
│   ├── request/      # Вхідні payload'и
│   ├── response/      # Вихідні payload'и
│   └── auth/          # Внутрішні результати автентифікації
├── security/         # JWT, OAuth2, фільтри, cookies
├── exception/         # Кастомні винятки
│   └── handler/        # GlobalExceptionHandler
├── mapper/            # MapStruct-мапери
├── validation/         # Кастомні @Constraint-валідатори
└── config/             # OpenAPI, security beans, app properties, Cloudinary
```

---

## База даних

Міграції керуються Flyway і виконуються автоматично при старті застосунку.

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
├── V10__create_password_reset_tokens_table.sql
├── V13__add_delivered_order_status.sql
├── V14__update_admin_password.sql
├── V15__update_admin_password.sql
├── V16__update_admin_password.sql
├── V17__update_admin_password.sql
├── V18__add_image_url_to_categories.sql
├── V19__seed_store_categories.sql
├── V20__seed_store_products.sql
├── V21__seed_store_customers.sql
├── V22__seed_store_carts.sql
├── V23__seed_store_orders.sql
└── V24__seed_store_favorites.sql
```

- **V19–V24** — синтетичні showcase-дані (15 категорій, 20 товарів, 18 покупців, кошики, 16 замовлень, вибране). Це звичайні міграції: вони застосовуються на кожній базі, включно з продакшеном, і не перезаписують наявні дані (`WHERE NOT EXISTS`).
- **V11 і V12** в основному наборі відсутні: ці номери використовують тестові дані в `src/test/resources/db/testdata`, які застосовуються лише в інтеграційних тестах (H2, Flyway `target: 18`).

Адмін за замовчуванням (`admin@verdora.com`) створюється міграцією V9; пароль змінювався міграціями V14–V17. Пароль у документації не публікується — отримайте його у відповідальних за середовище.

Детальніше — у [Database](./database/database.md).

---

## Swagger

- UI: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- OpenAPI spec: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

---

## Docker / CI/CD

У репозиторії є `Dockerfile` (multi-stage build), `docker-compose.yml` (PostgreSQL 16 + застосунок) і GitHub Actions workflows `.github/workflows/ci.yml` (build, тести, package, Spotless на кожен push і PR у `main`) та `cd.yml` (Render Deploy Hook на push у `main`). Flyway-міграції застосовуються самим застосунком при старті контейнера. Детальніше — [Docker](./docker/docker.md), [CI](./ci-cd/github-ci.md), [CD](./ci-cd/github-cd.md).

---

## Документація

- [Sign In](./auth/sign-in.md)
- [Sign Up](./auth/sign-up.md)
- [JWT](./auth/jwt.md)
- [Google OAuth](./auth/google-oauth.md)
- [Password Reset](./auth/password_reset.md)
- [Products](./products/products.md)
- [Categories](./categories/categories.md)
- [Cart](./cart/cart.md)
- [Orders](./order/orders.md)
- [Favorites](./favorites/favorites.md)
- [Database](./database/database.md)
- [Docker](./docker/docker.md)
- [CI](./ci-cd/github-ci.md)
- [CD](./ci-cd/github-cd.md)
