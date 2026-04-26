# E-Commerce RESTful API

Java 21 + Spring Boot 3 + PostgreSQL + Spring Boot + Swagger/OpenAPI

Backend e-commerce platform demonstrating REST API design, inventory management, ordering workflows, DTO-based API contracts, pagination/filtering, and production-style backend practices.

---

## Current Release

**Version:** `v1.1 API Hardening`

### Completed Milestones
- ✅ v1.0 Core REST API
- ✅ v1.1 API Hardening
- 🚧 v1.2 Database & Quality (Next)

---

## Features

### Core Commerce
- Product management
- Inventory stock tracking
- Order creation
- Stock reservation during order creation
- Payment processing simulation
- Order lifecycle handling

### API Hardening (v1.1)
- DTO-based API responses
- Pagination support
- Sorting support
- Product search filtering
- Price range filtering
- Request validation
- Custom API exception handling

### Platform
- PostgreSQL in Docker
- Swagger/OpenAPI documentation
- Layered architecture
- Java 21 records for DTOs

---

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Maven
- Swagger / OpenAPI

---

## Requirements

- Java 21
- Maven
- Docker Desktop

---

# Getting Started

## Start PostgreSQL

```bash
docker compose up -d
```

## Run API

```bash
mvn spring-boot:run
```

---

## Swagger UI

Open:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# API Endpoints

## Products
```http
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

### Product Query Features
```http
GET /api/products?page=0&size=10
GET /api/products?sortBy=price&sortDir=desc
GET /api/products?search=laptop
GET /api/products?minPrice=500&maxPrice=5000
```

---

## Inventory
```http
GET    /api/inventory
PATCH  /api/inventory/{productId}/stock
```

---

## Orders
```http
POST   /api/orders
GET    /api/orders
GET    /api/orders/{id}
```

---

## Payments
```http
POST   /api/payments
GET    /api/payments
```

---

# Example Requests

## Create Order

```json
{
  "customerName": "Michael",
  "customerEmail": "michael@example.com",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

---

## Process Payment

```json
{
  "orderId": 1,
  "paymentMethod": "CARD"
}
```

---

# Pagination Response Example

```json
{
  "content": [
    {
      "id": 1,
      "name": "Laptop",
      "price": 15000
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 25,
  "totalPages": 3,
  "last": false
}
```

---

# Project Roadmap

## Completed
- [x] DTO Refactor
- [x] Pagination / Sorting / Filtering

## Next (v1.2)
- [ ] Flyway migrations
- [ ] Integration tests with Testcontainers
- [ ] Auditing and optimistic locking

## Planned
- [ ] JWT Authentication
- [ ] Dockerized application container
- [ ] Kafka event-driven workflows

---

# Architecture (Current)
```text
Controller
  ↓
Service
  ↓
Repository
  ↓
PostgreSQL
```

Modules:
```text
Product
Inventory
Orders
Payments
```

---

## Running Tests

```bash
mvn test
```

---

## Future Enhancements
Planned evolution toward:

- Security with JWT
- Event-driven microservices
- Kafka integration
- CI/CD pipeline
- Observability and monitoring

---

## Author

Built by Michael Westman

GitHub:
https://github.com/mikeywestie/ecommerce-api
