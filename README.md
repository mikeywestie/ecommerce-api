# E-Commerce RESTful API

Java 21 + Spring Boot 3 + PostgreSQL + Swagger/OpenAPI.

## Features

- Product management
- Inventory stock tracking
- Order creation
- Stock reservation during order creation
- Payment processing simulation
- Order status updates
- Swagger UI
- PostgreSQL in Docker

## Requirements

- Java 21
- Maven
- Docker Desktop

## Run PostgreSQL

```bash
docker compose up -d
```

## Run the API

```bash
mvn spring-boot:run
```

## Swagger UI

Open:

```text
http://localhost:8080/swagger-ui/index.html
```

## Useful endpoints

```text
GET    /api/products
POST   /api/products
GET    /api/inventory
PATCH  /api/inventory/{productId}/stock
POST   /api/orders
GET    /api/orders
GET    /api/orders/{id}
POST   /api/payments
GET    /api/payments
```

## Example order request

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

## Example payment request

```json
{
  "orderId": 1,
  "paymentMethod": "CARD"
}
```
