# 🛒 E-Commerce RESTful API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-black)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![JWT](https://img.shields.io/badge/Security-JWT-success)
![Prometheus](https://img.shields.io/badge/Prometheus-Metrics-red)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-orange)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-blueviolet)

Production-style backend e-commerce platform built to showcase secure API development, event-driven architecture, observability, and DevOps practices.

The platform models realistic commerce workflows and is complemented by a React-based admin dashboard for operational visibility.

Frontend Admin Dashboard:  
➡️ https://github.com/mikeywestie/ecommerce-admin-ui

---

## 🎯 Project Purpose

This project demonstrates practical software engineering capabilities including:

- Layered Spring Boot architecture
- Secure REST API design
- JWT authentication and role-based access control
- DTO-based API contracts
- Bean validation and standardized ProblemDetail errors
- PostgreSQL persistence with Flyway migrations
- Optimistic locking and concurrency protection
- Kafka domain event publishing
- Docker Compose local infrastructure
- Spring Boot Actuator health and metrics
- Prometheus and Grafana observability
- GitHub Actions CI verification
- Testcontainers-based integration testing foundation

---

## 🚀 Current Release

**Latest Stable Release:** `v1.6.0 Observability Complete ✅`  
**Next Major Release:** `v2.0 Event-Driven Microservices 🚀`

---

## 🗂️ Release Milestones

Each milestone reflects an intentional architectural progression, prioritizing correctness, maintainability, and production readiness.

### Architectural Trade-Offs and Decisions

- **Modular Monolith First** — Simplifies development and debugging while preserving a clear migration path to microservices.
- **REST APIs Before Kafka Workflows** — Stabilizes business rules before introducing asynchronous orchestration.
- **DTO Mapping Over Entity Exposure** — Keeps persistence concerns separate from public API contracts.
- **Optimistic Locking** — Protects inventory updates while maintaining good throughput.
- **Docker Compose** — Provides a reproducible local development environment.
- **Metrics Before Dashboards** — Exposes reliable metrics before visualizing them.
- **Incremental Testing Strategy** — Builds a Testcontainers foundation before expanding coverage.

---

### v1.0 Core REST API ✅

Implemented foundational commerce capabilities.

**Features**
- Product catalog
- Inventory management
- Orders
- Payment simulation
- PostgreSQL persistence
- Swagger/OpenAPI documentation

**Why this milestone matters**  
Established the core domain model and API structure.

---

### v1.1 API Hardening ✅

Improved API usability and resilience.

**Features**
- DTO refactor
- Validation
- Pagination, sorting, and filtering
- ProblemDetail exception handling

**Why this milestone matters**  
Separated API contracts from entities and standardized error responses.

---

### v1.2 Database Quality ✅

Added production-quality data practices.

**Features**
- Flyway migrations
- Auditing fields
- Optimistic locking
- Concurrency protection
- Integration tests foundation

**Why this milestone matters**  
Supports repeatable schema evolution and stronger data integrity.

---

### v1.3 Security ✅

Secured the API.

**Features**
- JWT authentication
- Stateless Spring Security
- BCrypt password hashing
- Role-based authorization

**Roles**
- ADMIN
- CUSTOMER

**Why this milestone matters**  
Demonstrates modern API security patterns used in enterprise systems.

---

### v1.4 Commerce Features ✅

Implemented realistic commerce workflows.

**Features**
- Shopping cart
- Cart checkout flow
- Coupon engine
- Fixed and percentage discounts
- Kafka domain events

**Published Events**
- `order-created`
- `payment-processed`
- `coupon-applied`

**Why this milestone matters**  
Introduced event-driven thinking and richer business logic.

---

### v1.5 DevOps Foundation ✅

Operationalized the platform.

**Features**
- Docker Compose stack
- Multi-stage Docker builds
- GitHub Actions CI foundation
- Spring Boot Actuator
- Testcontainers foundation

**Why this milestone matters**  
Introduced deployment automation and operational tooling.

---

### v1.6 Observability Complete ✅

Added production-style monitoring.

**Features**
- Prometheus metrics endpoint
- Prometheus scraping
- Grafana dashboards
- JVM metrics
- HTTP metrics
- Database connection metrics
- Kafka metrics foundation

**Why this milestone matters**  
Provides visibility into application health and runtime behavior.

---

## 🏗️ Architecture

```text
React Admin UI
      │
      ▼
Spring Boot REST API
      │
 ┌────┼────────────┐
 ▼    ▼            ▼
JWT PostgreSQL   Kafka
      │            │
      ▼            ▼
Spring Actuator  Domain Events
      │
      ▼
Prometheus
      │
      ▼
Grafana
```

For a deeper portfolio-friendly explanation, see [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

---

## 🛣️ Roadmap

### v2.0 Event-Driven Microservices 🚀

#### Service Decomposition
- Product Service
- Order Service
- Payment Service
- Inventory Service

#### Messaging Patterns
- Saga Pattern
- Transactional Outbox
- Idempotent Consumers
- Dead Letter Queues

#### Platform Components
- API Gateway
- Service Discovery
- Distributed Configuration
- Distributed Tracing

#### Cloud Native
- Kubernetes
- Helm Charts
- GitOps
- Cloud Deployment

---

## 💼 Portfolio Value

This repository is designed to demonstrate full-stack backend capability beyond CRUD APIs. It shows practical experience with secure API design, integration patterns, data integrity, asynchronous messaging, Docker-based infrastructure, monitoring, and CI automation.
