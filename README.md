# 🛒 E-Commerce RESTful API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-black)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![JWT](https://img.shields.io/badge/Security-JWT-success)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-blueviolet)

Production-style backend e-commerce platform built to showcase enterprise Java development, secure API design, event-driven architecture, DevOps practices, and distributed systems foundations.

---

## 🎯 Project Purpose

This project demonstrates practical software engineering capabilities including:

- Designing clean layered architectures
- Building secure REST APIs
- Implementing JWT authentication and RBAC
- Applying optimistic locking and concurrency control
- Publishing domain events with Kafka
- Containerizing applications with Docker
- Automating builds with GitHub Actions
- Exposing metrics and health endpoints
- Writing production-grade code and documentation

This repository is complemented by the frontend admin dashboard:

➡️ **Admin UI Repository:** https://github.com/mikeywestie/ecommerce-admin-ui

---

## 🚀 Current Release

**Latest Stable Release:** `v1.5.0 DevOps Complete ✅`

**Next Major Release:** `v2.0 Event-Driven Microservices 🚀`

---

## 🗂️ Release Milestones

The frontend was developed iteratively, beginning with a static dashboard and evolving into a secure operations portal integrated with the backend APIs.

### Architectural Trade-Offs and Decisions

- **Static Mock Data First** — Allowed UI development to progress before backend endpoints were fully available.
- **LocalStorage for JWT Tokens** — Chosen for simplicity and transparency in a portfolio project.
- **Global Axios Interceptor** — Centralized token injection to avoid repeated authentication logic.
- **Protected Routes** — Encapsulated authentication checks in a reusable component.
- **Fixed Sidebar Layout** — Provides an enterprise-style navigation experience.
- **Client-Side Search and Pagination** — Suitable for current dataset sizes and can be moved server-side later.
- **Tailwind CSS** — Selected for rapid iteration and maintainable styling.


Each milestone reflects an intentional architectural progression. The platform was built iteratively, prioritizing correctness, maintainability, and production-readiness.

### Architectural Trade-Offs and Decisions

- **Modular Monolith First** — The application was intentionally developed as a modular monolith to simplify debugging and deployment while preserving a clean path to future microservice decomposition.
- **REST APIs Before Kafka Workflows** — Core business functionality was stabilized before introducing asynchronous event-driven messaging.
- **DTO Mapping Over Direct Entity Exposure** — Additional mapping code was accepted to decouple persistence models from API contracts.
- **Optimistic Locking** — Chosen over pessimistic locking to improve throughput while protecting inventory consistency.
- **Docker Compose** — Used to provide a repeatable local environment for PostgreSQL and Kafka.
- **Metrics Before Dashboards** — Prometheus endpoints were exposed first; Grafana dashboards are added later.
- **Incremental Testing Strategy** — Established a Testcontainers foundation before building extensive end-to-end test coverage.


### v1.0 Core REST API ✅

Implemented the foundational commerce domain.

**Features**
- Product Catalog
- Inventory Management
- Orders
- Payment Simulation
- PostgreSQL Persistence
- Swagger/OpenAPI Documentation

**Why this milestone matters**  
Established a clean domain model and API foundation before introducing advanced patterns.

---

### v1.1 API Hardening ✅

Improved API usability and resilience.

**Features**
- DTO Refactor
- Validation
- Pagination / Sorting / Filtering
- ProblemDetail Exception Handling

**Why this milestone matters**  
Separated persistence from API contracts and standardized error responses.

---

### v1.2 Database Quality ✅

Added production-quality data practices.

**Features**
- Flyway Migrations
- Auditing (`created_at`, `updated_at`)
- Optimistic Locking
- Concurrency Protection
- Integration Tests

**Why this milestone matters**  
Protects data integrity and ensures schema evolution is repeatable.

---

### v1.3 Security ✅

Secured the API.

**Features**
- JWT Authentication
- Stateless Spring Security
- BCrypt Password Hashing
- Role-Based Authorization

**Roles**
- ADMIN
- CUSTOMER

**Why this milestone matters**  
Demonstrates modern security design used in enterprise APIs.

---

### v1.4 Commerce Features ✅

Added realistic business functionality.

**Features**
- Shopping Cart
- Cart Checkout Flow
- Coupon Engine
- Fixed and Percentage Discounts
- Kafka Domain Events

**Published Events**
- `order-created`
- `payment-processed`
- `coupon-applied`

**Why this milestone matters**  
Introduced richer business workflows and event-driven foundations.

---

### v1.5 DevOps Complete ✅

Operationalized the platform.

**Features**
- Docker Compose Stack
- Multi-stage Docker Builds
- GitHub Actions CI/CD
- Spring Boot Actuator
- Prometheus Metrics
- Testcontainers Foundation

**Why this milestone matters**  
Demonstrates deployment automation, monitoring, and production readiness.

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
- Dead Letter Queues
- Idempotent Consumers

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

## 🏗️ Architecture

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
````

Patterns Implemented:

* REST APIs
* Repository Pattern
* DTO Mapping
* Optimistic Locking
* JWT Security
* Event Publishing
* Dockerized Deployment
* Observability

---

## 🔐 Security Model

### Public Endpoints

* `POST /api/auth/login`
* `POST /api/auth/register`
* `GET /api/products`

### ADMIN

* Products CRUD
* Inventory
* Coupons
* Dashboard

### CUSTOMER

* Orders
* Payments
* Cart

---

## 📡 Event Design

Current domain events:

* `order-created`
* `payment-processed`
* `coupon-applied`

Kafka provides asynchronous communication and a migration path to microservices.

---

## 🧰 Tech Stack

### Core

* Java 21
* Spring Boot 3
* Spring Security
* Spring Data JPA

### Data

* PostgreSQL
* Flyway

### Messaging

* Apache Kafka

### DevOps

* Docker
* Docker Compose
* GitHub Actions

### Observability

* Spring Boot Actuator
* Prometheus

### Testing

* JUnit 5
* Testcontainers

### Documentation

* Swagger/OpenAPI

---

## 📁 Project Structure

```text
src/main/java/com/mikey/ecommerce
├── cart
├── common
├── coupon
├── dashboard
├── dto
├── inventory
├── mapper
├── order
├── payment
├── product
└── security
```

---

## 🌐 Key Endpoints

| Endpoint                     | Description                  |
| ---------------------------- | ---------------------------- |
| `POST /api/auth/login`       | Authenticate and receive JWT |
| `GET /api/dashboard/summary` | Dashboard metrics            |
| `GET /api/orders`            | List orders                  |
| `GET /api/payments`          | List payments                |
| `GET /api/inventory`         | List inventory               |
| `GET /actuator/health`       | Health status                |
| `GET /actuator/prometheus`   | Prometheus metrics           |

---

## 🐳 Running the Application

### Start Infrastructure

```bash
docker compose up -d
```

Starts:

* PostgreSQL
* Kafka
* Zookeeper (if configured)
* API container (if included)

### Run Locally

```bash
mvn spring-boot:run
```

### Run Tests

```bash
mvn test
```

---

## 🐞 Debugging Tips

### Run with Debug Logging

```yaml
logging:
  level:
    org.springframework.web: DEBUG
    com.mikey.ecommerce: DEBUG
```

### Common Commands

```bash
mvn clean package -DskipTests
```

```bash
docker logs ecommerce-api
```

```bash
Invoke-RestMethod http://localhost:8080/actuator/health
```

---

## 📚 Helpful Resources

* Spring Boot: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
* Spring Security: [https://spring.io/projects/spring-security](https://spring.io/projects/spring-security)
* Kafka: [https://kafka.apache.org/](https://kafka.apache.org/)
* Flyway: [https://flywaydb.org/](https://flywaydb.org/)
* Docker: [https://www.docker.com/](https://www.docker.com/)
* Testcontainers: [https://testcontainers.com/](https://testcontainers.com/)

---

## 👨‍💻 Author

**Michael Westman**

* GitHub: [https://github.com/mikeywestie](https://github.com/mikeywestie)
* LinkedIn: [https://www.linkedin.com/in/michael-westman-219178188/](https://www.linkedin.com/in/michael-westman-219178188/)

---

## ⭐ Support

If you found this project useful, please consider starring the repository.