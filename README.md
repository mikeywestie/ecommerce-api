# E-Commerce RESTful API

Java 21 + Spring Boot 3 + PostgreSQL + Kafka + Swagger/OpenAPI

Production-style backend e-commerce platform showcasing modern API development, database reliability, security, event-driven workflows, testing, and concurrency controls.

---

# Current Release

**Latest Stable Release:** `v1.4.0 Commerce Features`

**Next Milestone:** `v1.5 DevOps`

---

# Release Milestones

## v1.0 Core REST API ✅
Foundational commerce domain and REST API implementation.

### Includes
- Product management
- Inventory tracking
- Order creation
- Payment simulation
- Swagger/OpenAPI documentation
- PostgreSQL Docker setup

---

## v1.1 API Hardening ✅
Focused on improving API contract quality and scalability.

### Includes
- DTO refactor
- Pagination
- Sorting
- Filtering
- Request validation
- Global exception handling

---

## v1.2 Database & Quality ✅
Focused on reliability and integrity.

### Includes
- Flyway migrations
- Integration testing
- Auditing support
- Optimistic locking
- Concurrency protection

---

## v1.3 Security ✅
Focused on authentication and access control.

### Includes
- JWT Authentication
- Spring Security
- Stateless security configuration
- BCrypt password hashing
- JWT request filtering
- Role-based authorization
- Protected endpoints

### Roles
```text
ADMIN
CUSTOMER
```

Access control:

```text
ADMIN
- Products
- Inventory
- Coupons

CUSTOMER
- Orders
- Payments
- Shopping Cart
```

---

## v1.4 Commerce Features ✅

### v1.4.1 Shopping Cart
- Customer carts
- Cart items
- Cart checkout
- Cart-to-order flow

### v1.4.2 Discounts & Coupons
- Coupon engine
- Percentage discounts
- Fixed discounts
- Discounted checkout totals
- Discount persistence on orders

### v1.4.3 Kafka Order Events
Published domain events:

```text
order-created
payment-processed
coupon-applied
```

Includes:
- Kafka producers
- Kafka consumers
- Event serialization
- Event-driven workflow foundations

---

# Features

## Commerce
- Product catalog management
- Inventory reservation
- Order workflow
- Payment simulation
- Shopping cart
- Coupon discounts
- Checkout flow

## API Design
- DTO mappings
- Pagination / filtering / sorting
- Validation
- ProblemDetail error responses

## Database Reliability
- Flyway migrations
- Auditing
- Optimistic locking
- Concurrency protection

## Security
- JWT Authentication
- Role-Based Access Control
- Protected endpoints
- BCrypt password hashing

## Event Driven
- Kafka integration
- Domain event publishing
- Event consumers

## Testing
- Integration tests

---

# Architecture Highlights

```text
Controller
 ↓
Service
 ↓
Repository
 ↓
PostgreSQL
```

Patterns implemented:

```text
REST APIs
DTO Mapping
Repository Pattern
Flyway Migrations
Integration Testing
Optimistic Locking
JWT Authentication
Role Authorization
Event Publishing
```

---

# Roadmap

## Completed
- [x] DTO Refactor
- [x] Pagination / Filtering
- [x] Flyway Migrations
- [x] Integration Tests
- [x] Auditing Support
- [x] Optimistic Locking
- [x] JWT Authentication
- [x] Role-Based Authorization
- [x] Shopping Cart
- [x] Discount / Coupon Engine
- [x] Kafka Order Events

---

## Upcoming

## v1.5 DevOps 🚧

Planned:
- [ ] Dockerized Spring Boot container
- [ ] Multi-stage Docker builds
- [ ] GitHub Actions CI/CD pipeline
- [ ] Monitoring / observability
- [ ] Testcontainers integration tests

---

## v2.0 Event Driven Microservices (Future)
Planned:

- [ ] Product Service
- [ ] Order Service
- [ ] Inventory Service
- [ ] Payment Service
- [ ] Kafka choreography
- [ ] Saga patterns
- [ ] API Gateway

---

# Version Timeline

```text
v1.0 Core REST API          ✅
v1.1 API Hardening          ✅
v1.2 Database & Quality     ✅
v1.3 Security               ✅
v1.4 Commerce Features      ✅
v1.5 DevOps                 Next
v2.0 Microservices          Future
```

---

# Security Design

Powered by Spring Security.

Includes:
- JWT login
- Token validation
- Authorization filter
- ADMIN / CUSTOMER roles
- Protected endpoints

Authorization:

```text
Public
GET products
Auth endpoints

ADMIN
Products
Inventory
Coupons

CUSTOMER
Orders
Payments
Cart
```

---

# Event Design

Current domain events:

```text
order-created
payment-processed
coupon-applied
```

Kafka-based producer/consumer architecture implemented as foundation for future event-driven decomposition.

---

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Apache Kafka
- Docker
- Maven
- Swagger/OpenAPI
- JUnit

---

# Running the Application

## Start Infrastructure
```bash
docker compose up -d
```

Starts:
- PostgreSQL
- Kafka
- Zookeeper

---

## Run API
```bash
mvn spring-boot:run
```

---

## Run Tests
```bash
mvn test
```

---

Swagger:
```text
http://localhost:8080/swagger-ui/index.html
```

---

# Future Focus
Current focus is evolving this project toward:

- DevOps readiness
- Event-driven architecture
- Microservices decomposition
- Cloud-native deployment

---

# Author

Built by Michael Westman

GitHub:
https://github.com/mikeywestie/ecommerce-api
