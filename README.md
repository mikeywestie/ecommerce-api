# E-Commerce RESTful API

Java 21 + Spring Boot 3 + PostgreSQL + Swagger/OpenAPI

Production-style backend e-commerce platform showcasing modern API development, database reliability, security, testing, and concurrency controls.

---

# Current Release

**Latest Stable Release:** `v1.2.0 Database & Quality`

**Current Development:** `v1.3 Security (In Progress)`

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

### Why it matters
Establishes the domain model and baseline commerce workflows before optimization and hardening.

---

## v1.1 API Hardening ✅
Focused on improving API contract quality and scalability.

### Includes
- DTO refactor (remove entity exposure)
- Pagination
- Sorting
- Filtering
- Request validation
- Improved exception handling

### Why it matters
Moves the API from prototype-level toward production-grade API design.

---

## v1.2 Database & Quality ✅
Focused on reliability, data integrity, and engineering quality.

### Includes
- Flyway database migrations
- Integration testing
- Auditing support
- Optimistic locking
- Concurrency protection

### Why it matters
Protects against schema drift, overselling, stale updates, and improves confidence through testing.

---

## v1.3 Security 🚧 (Current)
Focused on authentication and access control.

### Completed
- [x] JWT Authentication
- [x] Stateless security configuration
- [x] Password hashing (BCrypt)
- [x] Protected API endpoints

### In Progress
- [ ] Role-Based Authorization (ADMIN / CUSTOMER)

### Why it matters
Secures the platform and introduces real-world access control patterns.

---

# Features

## Commerce
- Product catalog management
- Inventory stock reservation
- Order workflow
- Payment processing simulation

## API Design
- DTO mappings
- Pagination, filtering, sorting
- Validation
- ProblemDetail error responses

## Database Reliability
- Flyway migrations
- Auditing
- Optimistic locking
- Concurrency safety

## Security
- JWT Authentication
- Stateless request security
- BCrypt password hashing
- Protected endpoints
- Role authorization (in progress)

## Platform
- PostgreSQL in Docker
- Swagger/OpenAPI
- Java 21 Records
- Layered architecture

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
Concurrency Protection
JWT Authentication
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

## In Progress
- [ ] Role-Based Authorization

## Planned
### v1.4 Commerce Enhancements
- Discounts / promotions
- Order events
- Event-driven workflows with Kafka

### v1.5 DevOps
- Dockerized app container
- CI/CD pipeline
- Monitoring / observability

---

# Version Timeline

```text
v1.0 Core REST API          ✅
v1.1 API Hardening          ✅
v1.2 Database & Quality     ✅
v1.3 Security               🚧
```

---

# Security Design (Current)

Authentication powered by Spring Security.

Includes:
- JWT login
- Token validation
- Protected endpoints
- ADMIN / CUSTOMER role model (in progress)

Upcoming:
```text
ADMIN:
Product + Inventory management

CUSTOMER:
Orders + Payments
```

---

# Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- PostgreSQL
- Flyway
- Docker
- Maven
- Swagger/OpenAPI

---

# Running the Application

## Start Database
```bash
docker compose up -d
```

## Run API
```bash
mvn spring-boot:run
```

## Run Tests
```bash
mvn test
```

---

# Author
Built by Michael Westman

GitHub:
https://github.com/mikeywestie/ecommerce-api
