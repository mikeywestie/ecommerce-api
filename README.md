# E-Commerce RESTful API

Java 21 + Spring Boot 3 + PostgreSQL + Swagger/OpenAPI

Backend e-commerce platform showcasing production-style API development, data integrity, testing, and concurrency controls.

---

# Current Release

**Latest Release:** `v1.2.0 Database & Quality`

## Completed Releases
- ✅ v1.0 Core REST API
- ✅ v1.1 API Hardening
- ✅ v1.2 Database & Quality

## Next Release
- 🚧 v1.3 Security

---

# Features

## Core Commerce
- Product management
- Inventory stock tracking
- Order creation workflow
- Payment processing simulation

## API Hardening
- DTO-based responses
- Pagination, sorting, filtering
- Validation and exception handling

## Database & Quality
- Flyway migrations
- Integration testing
- Entity auditing
- Optimistic locking for inventory concurrency

## Platform
- PostgreSQL in Docker
- Swagger/OpenAPI
- Java 21 records
- Layered architecture

---

# Roadmap

## Completed
- [x] DTO Refactor
- [x] Product Pagination / Filtering
- [x] Flyway Database Migrations
- [x] Integration Tests
- [x] Auditing Support
- [x] Optimistic Locking

## In Progress (Upcoming)
### v1.3 Security
- [ ] JWT Authentication
- [ ] Role-Based Authorization (ADMIN / CUSTOMER)

## Planned Later
- [ ] Kafka event-driven workflows
- [ ] Dockerized application container
- [ ] CI/CD pipeline
- [ ] Monitoring and observability

---

# Architecture Highlights

Current project includes:

```text
REST APIs
DTO Mapping
Repository Pattern
Flyway Migrations
Integration Testing
Concurrency Protection
```

---

# Version Timeline

```text
v1.0 Core REST API        ✅
v1.1 API Hardening        ✅
v1.2 Database & Quality   ✅
v1.3 Security             Next
```

---

## Upcoming Security Features
Planned with :contentReference[oaicite:0]{index=0}

- JWT login
- Token validation
- ADMIN / CUSTOMER roles
- Protected endpoints
