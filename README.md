# Awesome Pizza Order Management Portal

> Java 17+ required.

A Spring Boot REST API for managing pizza orders without user registration.

## Quick Start

Run to start the application on `http://localhost:8080`:
```bash
./mvnw spring-boot:run
```

## Configuration Options

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | 8080 | Server port to bind to |
| `DATABASE_URL` | `jdbc:h2:mem:pizzadb;DB_CLOSE_DELAY=-1` | Database connection string |
| `SPRING_JPA_HIBERNATE_DDL_MODE` | `validate` | Schema generation strategy |
| `DEBUG` | false | Enable debug logging (set to true) |

Set via `application.properties` file

## API Reference

- **Customer APIs** (no auth required):
  - `POST /api/customers/orders` - Place new order
  - `GET /api/customers/orders/{orderId}` - Track order status
  - `DELETE /api/customers/orders/{orderId}` - Cancel pending order (PENDING only)

- **Chef APIs**:
  - `GET /api/chef/queue` - View pending order queue (paginated, defaults: page 0, size 20, sort by createdAt ASC)
  - `GET /api/chef/orders/{id}` - Get an order by ID
  - `DELETE /api/chef/orders/{id}` - Force cancel an order at any status
  - `PATCH /api/chef/orders/{id}/start` - Start cooking (PENDING → COOKING)
  - `PATCH /api/chef/orders/{id}/complete` - Mark order ready (COOKING → READY)

- **Catalog API**:
  - `GET /api/pizza-types` - List available pizzas with pricing

## API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui.html` when the application is running.

- **Interactive UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
  - Try out API endpoints directly in browser
  - View request/response schemas and data models
  - See parameters, descriptions, and validation rules
  - Test calls with built-in client

- **OpenAPI Specification**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
  - Raw JSON spec for API consumers and tooling
  - Available at `/v2/api-docs` as well (legacy OpenAPI 2.0 format)

The documentation is auto-generated from your controller annotations (`@PostMapping`, `@GetMapping`, etc.) and request/response bodies.
