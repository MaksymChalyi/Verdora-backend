# Verdora Backend

Backend service built with Spring Boot. Provides authentication, user management, and unified API responses.

---

## 1. Overview

- Cookie-based authentication
- Unified API response format
- RESTful endpoints
- Swagger documentation

---

## 2. Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Swagger (springdoc)

---

## 3. API Contract

### 3.1 Response Format

All endpoints return:

```
{
"timestamp": "2026-04-21T13:55:49.772Z",
"status": 200,
"message": "Success",
"data": {}
}
```
### 3.2 Error Format
```
{
"timestamp": "2026-04-21T13:55:49.773Z",
"status": 400,
"message": "Error message",
"data": null
}
```
---

## 4. Authentication

### Flow

1. Login → sets cookies:
    - accessToken
    - refreshToken

2. Requests → use cookies automatically

3. Refresh → generates new accessToken

4. Logout → clears cookies

---

## 5. Endpoints

### 5.1 Auth

| Method | Endpoint        | Description        |
|--------|----------------|--------------------|
| POST   | /auth/register | Register user      |
| POST   | /auth/login    | Login              |
| POST   | /auth/refresh  | Refresh token      |
| POST   | /auth/logout   | Logout             |

---

### 5.2 User

| Method | Endpoint              | Description              |
|--------|----------------------|--------------------------|
| GET    | /users/current-user  | Get current user email   |

---

### 5.3 Health

| Method | Endpoint        | Description              |
|--------|----------------|--------------------------|
| GET    | /health/ping   | Service availability     |

---

## 6. Validation

Used annotations:

- @Valid
- @Email
- @NotBlank

---

## 7. Error Handling

Centralized via:

- GlobalExceptionHandler
- BaseException
- Spring Security exceptions

---

## 8. Swagger

- UI: http://localhost:8081/swagger-ui/index.html
- OpenAPI: /v3/api-docs

---

## 9. Database

- PostgreSQL
- Flyway migrations

---

## 10. Run Application

### 10.1 Prerequisites

- Java 21
- Maven
- PostgreSQL

---

### 10.2 Database Setup

Create database:

CREATE DATABASE verdora;

---

### 10.3 Configuration

Configure datasource in application.yml:

spring:
datasource:
url: jdbc:postgresql://localhost:5432/verdora
username: postgres
password: postgres

jpa:
hibernate:
ddl-auto: validate

flyway:
enabled: true

---

### 10.4 Run Application

mvn clean install  
mvn spring-boot:run

or

./mvnw spring-boot:run

---

### 10.5 Access

- API: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui/index.html

---

### 10.6 Health Check

GET /health/ping

Expected:
```
{
"status": 200,
"message": "Service is alive",
"data": "pong"
}
```
---

## 11. Project Structure

controller/
service/
repository/
dto/
exception/
security/

---

## 12. Future Improvements

- Add DTO for current user (id, role)
- Replace Principal with custom UserDetails
- Add refresh token rotation
- Add role-based access control
- Add integration tests

---

## 13. Notes

- Uses cookies instead of Bearer tokens
- All responses wrapped in BaseResponse
- Swagger examples defined manually
