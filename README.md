# 🛒 E-Commerce RESTful API

![Architecture_Diagram.png](docs/images/Architecture_Diagram.png)

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-black)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![JWT](https://img.shields.io/badge/Security-JWT-success)
![Prometheus](https://img.shields.io/badge/Prometheus-Metrics-red)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-orange)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-blueviolet)

## 📚 API Documentation

The platform exposes a fully documented REST API using OpenAPI 3 and Swagger UI. The screenshots below showcase the available endpoints across the different business domains.

### Swagger UI Overview
![Swagger UI](docs/images/swagger-ui.png)

### Products Controller
![Products Endpoints](docs/images/Products_Endpoints.png)

### Payments Controller
![Payments Endpoints](docs/images/Payments_Endpoints.png)

### Orders Controller
![Orders Endpoints](docs/images/Orders_Endpoints.png)

### Coupon Controller
![Coupon Endpoints](docs/images/Coupon_Endpoints.png)

### Cart Controller
![Cart Endpoints](docs/images/Cart_Endpoints.png)

### Authentication Controller
![Auth Endpoints](docs/images/Auth_Endpoints.png)

### Inventory Controller
![Inventory Endpoints](docs/images/Inventory_Endpoints.png)

### Dashboard Controller
![Dashboard Endpoints](docs/images/Dashboard_Endpoints.png)

---

## 🐳 Containerized Infrastructure

The entire development environment is orchestrated with Docker Compose, including PostgreSQL, Apache Kafka, Prometheus, and Grafana.

![Docker Containers](docs/images/Docker_Container.png)

---

## ❤️ Application Health

Spring Boot Actuator provides health, liveness, and readiness endpoints to ensure the application and all dependencies are operating correctly.

![Actuator Health](docs/images/Actuator_Health.png)

---

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

## 🔁 System Flow

The platform models an end-to-end commerce flow, from authenticated API usage through order placement, payment processing, inventory updates, and operational monitoring.

```text
User / Admin Dashboard
        │
        ▼
Authenticate via JWT
        │
        ▼
Create cart / place order
        │
        ▼
Order workflow validates business rules
        │
        ▼
Domain event published to Kafka
        │
        ▼
Payment workflow processes transaction outcome
        │
        ▼
Inventory is updated with concurrency protection
        │
        ▼
Application metrics emitted through Actuator
        │
        ▼
Prometheus scrapes metrics
        │
        ▼
Grafana visualizes runtime health and behaviour
```

This flow is intentionally designed to show how a traditional REST API can evolve toward event-driven order management while still keeping the current implementation easy to run, test, and reason about locally.

---

## 🧪 API Usage Examples

### Authenticate

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "<user-email>",
  "password": "<your-password>"
}
```

Example response:

```json
{
  "token": "<jwt-token>"
}
```

### Create an Order

```http
POST /api/orders
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

```json
{
  "customerName": "Michael Westman",
  "customerEmail": "michael@example.com",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "couponCode": "WELCOME10"
}
```

### Process a Payment

```http
POST /api/payments
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

```json
{
  "orderId": 1,
  "paymentMethod": "CARD",
  "amount": 1299.99
}
```

### Update Inventory

```http
PATCH /api/inventory/{productId}
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

```json
{
  "quantityAvailable": 25
}
```

These examples are intended to make the project easy to explore during code reviews, technical interviews, and portfolio walkthroughs.

---

## 🧠 Engineering Challenges Solved

This project is not only a CRUD API. It intentionally includes practical backend engineering concerns that appear in real commerce and order management systems.

| Challenge | Approach |
| --- | --- |
| Preventing accidental entity exposure | DTO-based API contracts separate persistence models from external responses. |
| Standardizing validation and error responses | Bean Validation and Spring `ProblemDetail` provide consistent API feedback. |
| Protecting inventory updates | Optimistic locking and concurrency-aware persistence reduce overselling risk. |
| Securing protected resources | JWT-based stateless authentication and role-based authorization protect API operations. |
| Supporting repeatable database changes | Flyway migrations make schema evolution trackable and reproducible. |
| Introducing asynchronous workflows | Kafka domain events demonstrate event-driven architecture foundations. |
| Running infrastructure locally | Docker Compose provisions PostgreSQL, Kafka, Prometheus, and Grafana. |
| Monitoring runtime behaviour | Actuator, Prometheus, and Grafana expose health, JVM, HTTP, and database metrics. |
| Building toward production readiness | CI verification and Testcontainers foundations support safer future changes. |

These decisions help demonstrate maintainability, scalability thinking, operational awareness, and clean backend design.

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
