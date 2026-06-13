# Ecommerce Platform Architecture

This document explains the architecture behind the Ecommerce Platform portfolio project.

## High-Level Overview

The platform is designed as a production-style ecommerce ecosystem with a Spring Boot backend, React admin/customer UI, PostgreSQL persistence, Kafka domain events, build metadata, GitHub issue based bug reporting, and observability through Spring Boot Actuator with Prometheus/Grafana support.

```text
React Commerce Platform UI
(Admin Portal + Customer Storefront)
        │
        ▼
Spring Boot REST API
        │
 ┌──────┼──────────────┬──────────────┐
 ▼      ▼              ▼              ▼
JWT   PostgreSQL     Kafka        GitHub Issues
        │              │              │
        ▼              ▼              ▼
Flyway Migrations  Domain Events  Bug Reports
        │
        ▼
Spring Actuator / Build Info
        │
        ▼
Prometheus / Grafana Support
```

## Core Components

### React Commerce Platform UI
Provides both an admin operations portal and a customer storefront for browsing products, managing cart items, checking out, viewing order history, and reporting bugs.

### Spring Boot REST API
Exposes secure REST endpoints for authentication, products, inventory, cart checkout, orders, payments, coupons, dashboard metrics, system health, build metadata, and bug reporting.

### PostgreSQL
Stores application data using a relational model with Flyway-managed schema migrations.

### JWT Security
Protects API endpoints using stateless authentication and role-based access control.

### Kafka
Publishes domain events such as order creation, payment processing, and coupon application. This prepares the platform for future microservice decomposition.

### GitHub Issue Reporting
The application can create GitHub issues from in-app bug reports while including route, user role, frontend/backend versions, commit hashes, browser details, and reproduction steps.

### Spring Boot Actuator
Exposes health, readiness, liveness, metrics, and Prometheus endpoints.

### Prometheus and Grafana
Available through Docker Compose for future operational monitoring and alerting improvements.

## Design Decisions

- Start as a modular monolith to keep development focused and maintainable.
- Use DTOs to prevent persistence entities from leaking into public API contracts.
- Use Flyway for repeatable schema evolution.
- Reserve inventory only after successful payment to model realistic payment behavior.
- Use Kafka events to prepare for distributed workflows.
- Use Docker Compose to make the local infrastructure reproducible.
- Expose build metadata so deployed versions can be verified from the UI.
- Use GitHub issues for bug-report visibility and portfolio-friendly product thinking.

## Future Architecture Direction

The next major milestone is to evolve the project into event-driven microservices:

- Product Service
- Order Service
- Payment Service
- Inventory Service
- API Gateway
- Distributed tracing
- Transactional outbox pattern
- Saga orchestration
- Idempotent consumers
- Dead letter queues
