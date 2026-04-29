# E-Commerce RESTful API

![CI](https://github.com/mikeywestie/ecommerce-api/actions/workflows/ci.yml/badge.svg)

Java 21 + Spring Boot 3 + PostgreSQL + Kafka + Docker

Production-style backend e-commerce platform showcasing secure API development, event-driven workflows, DevOps engineering, observability, and distributed systems foundations.

---

# Current Release

**Latest Stable Release:** `v1.5.0 DevOps Complete` ✅  
**Next Major Release:** `v2.0 Event-Driven Microservices`

---

# Release Milestones

## v1.0 Core REST API ✅

- Products
- Inventory
- Orders
- Payment simulation
- Swagger/OpenAPI
- PostgreSQL

---

## v1.1 API Hardening ✅

- DTO Refactor
- Pagination / Sorting / Filtering
- Validation
- ProblemDetail exception handling

---

## v1.2 Database & Quality ✅

- Flyway migrations
- Integration testing
- Auditing
- Optimistic locking
- Concurrency protection

---

## v1.3 Security ✅

- JWT Authentication
- Stateless Spring Security
- BCrypt password hashing
- Role-Based Authorization

### Roles

```text
ADMIN
CUSTOMER
```

---

## v1.4 Commerce Features ✅

### Shopping Cart
- Cart checkout flow
- Cart items
- Cart-to-order

### Discounts / Coupons
- Percentage discounts
- Fixed discounts
- Coupon engine
- Discount persistence

### Kafka Order Events

Published domain events:

```text
order-created
payment-processed
coupon-applied
```

---

## v1.5 DevOps ✅

### Completed
- Dockerized application stack
- Multi-stage Docker builds
- GitHub Actions CI/CD
- Observability / Actuator / Prometheus metrics
- Testcontainers integration foundation

### DevOps Features
- Docker Compose stack
- PostgreSQL + Kafka containers
- CI build pipeline
- Health probes
- Metrics endpoint
- Prometheus support

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

## Security
- JWT Authentication
- Role-Based Access Control
- Protected endpoints

## Event Driven
- Kafka producers
- Kafka consumers
- Domain events
- Event workflow foundations

## DevOps
- Dockerized runtime
- CI/CD pipeline
- Observability
- Testcontainers foundation

---

# Architecture

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
Repository Pattern
DTO Mapping
Optimistic Locking
JWT Security
Event Publishing
Dockerized Deployment
Observability
```

---

# Roadmap

## Completed ✅

- [x] DTO Refactor
- [x] Flyway Migrations
- [x] Integration Tests
- [x] JWT Authentication
- [x] Role-Based Authorization
- [x] Shopping Cart
- [x] Discount / Coupon Engine
- [x] Kafka Order Events
- [x] Dockerized Deployment
- [x] GitHub Actions CI/CD
- [x] Observability / Prometheus Metrics
- [x] Testcontainers Foundation

---

# v2.0 Event-Driven Microservices 🚀

## Milestone 2.1 Service Decomposition
- [ ] Split Product Service
- [ ] Split Order Service
- [ ] Split Inventory Service
- [ ] Split Payment Service

## Milestone 2.2 Messaging Patterns
- [ ] Kafka choreography
- [ ] Saga pattern
- [ ] Transactional Outbox Pattern
- [ ] Idempotent Consumers
- [ ] Retry / Dead Letter Queues

## Milestone 2.3 Platform
- [ ] API Gateway
- [ ] Service Discovery
- [ ] Distributed Configuration
- [ ] Distributed Tracing

## Milestone 2.4 Observability+
- [ ] Prometheus Dashboards
- [ ] Grafana Dashboards
- [ ] Centralized Logging
- [ ] Trace Correlation

## Milestone 2.5 Cloud Native
- [ ] Kubernetes Deployment
- [ ] Helm Charts
- [ ] GitOps
- [ ] Cloud Deployment

---

# Version Timeline

```text
v1.0 Core REST API         ✅
v1.1 API Hardening         ✅
v1.2 Database Quality      ✅
v1.3 Security              ✅
v1.4 Commerce Features     ✅
v1.5 DevOps                ✅
v2.0 Microservices         🚀
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

Authorization model:

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
- GitHub Actions
- Prometheus
- JUnit
- Testcontainers
- Swagger/OpenAPI

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

## Swagger

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Actuator

```text
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/prometheus
```

---

# Future Focus

Moving toward:

```text
Distributed Systems
Event-Driven Microservices
Cloud Native Deployment
Production Observability
```

---

# Author

Built by Michael Westman

GitHub:  
https://github.com/mikeywestie/ecommerce-api