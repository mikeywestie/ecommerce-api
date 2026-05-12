# Ecommerce Platform Architecture

This document explains the architecture behind the Ecommerce Platform portfolio project.

## High-Level Overview

The platform is designed as a production-style ecommerce ecosystem with a Spring Boot backend, React admin dashboard, PostgreSQL persistence, Kafka domain events, and observability through Prometheus and Grafana.

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

## Core Components

### React Admin UI
Provides an operational dashboard for viewing orders, payments, inventory, and system health.

### Spring Boot REST API
Exposes secure REST endpoints for ecommerce workflows including product management, cart checkout, orders, payments, coupons, and inventory.

### PostgreSQL
Stores application data using a relational model with Flyway-managed schema migrations.

### JWT Security
Protects API endpoints using stateless authentication and role-based access control.

### Kafka
Publishes domain events such as order creation, payment processing, and coupon application. This prepares the platform for future microservice decomposition.

### Spring Boot Actuator
Exposes health, readiness, liveness, metrics, and Prometheus endpoints.

### Prometheus and Grafana
Collect and visualize runtime metrics to simulate production-style observability.

## Design Decisions

- Start as a modular monolith to keep development focused and maintainable.
- Use DTOs to prevent persistence entities from leaking into public API contracts.
- Use Flyway for repeatable schema evolution.
- Use optimistic locking to protect inventory updates.
- Use Kafka events to prepare for distributed workflows.
- Use Docker Compose to make the local infrastructure reproducible.

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
