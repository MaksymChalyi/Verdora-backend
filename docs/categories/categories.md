# Categories

## Overview

Endpoints for viewing and managing product categories.

Public endpoints are available without authentication. Category management and the admin category list require the `ADMIN` role.

## Entity

```java
Category {
  id    Long    // auto-generated
  name  String  // max 256 chars, required
}
```

Database table: `categories`

```sql
CREATE TABLE categories (
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(256) NOT NULL
);
```

## Endpoints

| Method | Endpoint            | Description                      | Auth     |
| ------ | ------------------- | -------------------------------- | -------- |
| GET    | `/categories`       | Get paginated list of categories | ❌       |
| GET    | `/categories/{id}`  | Get category by ID               | ❌       |
| GET    | `/admin/categories` | Get all categories for admin     | 🔒 ADMIN |
| POST   | `/categories`       | Create category                  | 🔒 ADMIN |
| PUT    | `/categories/{id}`  | Update category                  | 🔒 ADMIN |
| DELETE | `/categories/{id}`  | Delete category                  | 🔒 ADMIN |

---

### GET `/categories` — Get all categories

Returns a paginated list of categories.

**Authentication:** not required

**Default page size:** 12

**Success response `200`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 200,
  "message": "Categories fetched successfully",
  "data": {
    "content": [
      {
        "categoryId": "1",
        "name": "Electronics"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 12,
    "number": 0
  }
}
```

---

### GET `/categories/{id}` — Get category by ID

**Path variable:** `id` — category ID

**Authentication:** not required

**Success response `200`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 200,
  "message": "Category fetched successfully",
  "data": {
    "categoryId": "1",
    "name": "Electronics"
  }
}
```

**Error response `404`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 404,
  "message": "Category with id 1 not found",
  "data": null
}
```

---

### GET `/admin/categories` — Get all categories

Returns all categories for the admin category management view.

**Authentication:** required

**Role:** `ADMIN`

**Success response `200`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 200,
  "message": "Categories fetched successfully",
  "data": [
    {
      "categoryId": "1",
      "name": "Electronics"
    }
  ]
}
```

If there are no categories, the endpoint returns an empty list:

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 200,
  "message": "Categories fetched successfully",
  "data": []
}
```

---

### POST `/categories` — Create

**Authentication:** required

**Role:** `ADMIN`

**Request body**

```json
{
  "name": "Electronics"
}
```

| Field  | Required | Validation               |
| ------ | -------- | ------------------------ |
| `name` | ✅       | not blank, max 256 chars |

**Success response `201`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 201,
  "message": "Category created",
  "data": {
    "categoryId": "1",
    "name": "Electronics"
  }
}
```

---

### PUT `/categories/{id}` — Update

**Authentication:** required

**Role:** `ADMIN`

**Path variable:** `id` — category ID

**Request body**

```json
{
  "name": "Home Appliances"
}
```

**Success response `200`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 200,
  "message": "Category updated",
  "data": {
    "categoryId": "1",
    "name": "Home Appliances"
  }
}
```

**Error response `404`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 404,
  "message": "Category with id 1 not found",
  "data": null
}
```

---

### DELETE `/categories/{id}` — Delete

**Authentication:** required

**Role:** `ADMIN`

**Path variable:** `id` — category ID

**Success response `200`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 200,
  "message": "Category deleted successfully",
  "data": null
}
```

**Error response `404`**

```json
{
  "timestamp": "2026-04-26T12:00:00Z",
  "status": 404,
  "message": "Category with id 1 not found",
  "data": null
}
```

---

## Flow

```mermaid
sequenceDiagram
    actor Client
    participant CategoryController
    participant CategoryServiceImpl
    participant CategoryRepository
    participant CategoryMapper

    Client->>CategoryController: POST /categories { name }

    CategoryController->>CategoryServiceImpl: createCategory(request)
    CategoryServiceImpl->>CategoryMapper: toEntity(request)
    CategoryMapper-->>CategoryServiceImpl: Category entity
    CategoryServiceImpl->>CategoryRepository: save(category)
    CategoryRepository-->>CategoryServiceImpl: saved Category
    CategoryServiceImpl->>CategoryMapper: toResponse(saved)
    CategoryMapper-->>CategoryServiceImpl: CategoryResponse
    CategoryServiceImpl-->>CategoryController: CategoryResponse
    CategoryController-->>Client: 201 { categoryId, name }
```

## Key Components

### `CategoryMapper`

MapStruct mapper — converts between `CategoryRequest` → `Category` entity → `CategoryResponse`. No manual mapping needed.

### `CategoryServiceImpl#getByIdOrThrow`

Reusable private method used by both `updateCategory` and `deleteCategory`:

```java
private Category getByIdOrThrow(Long id) {
    return categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));
}
```

If category is not found — throws `CategoryNotFoundException`, which is handled by `GlobalExceptionHandler` and returns `404`.
