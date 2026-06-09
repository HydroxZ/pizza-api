# Awesome Pizza Order Management Portal

[![Version](https://img.shields.io/badge/version-1.0.0-blue)](#)
[![Build Status](https://img.shields.io/badge/build-passing-green)](#)
[![License](https://img.shields.io/badge/license-MIT-orange)](#)

> **Maintenance**: This project is actively maintained. Java 17+ required.

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
  - `POST /api/orders` - Place new order
  - `GET /api/orders/{id}` - Track order status
  - `DELETE /api/orders/{id}/cancel` - Cancel pending order

- **Chef APIs**:
  - `GET /api/orders/current` - Get first order in queue (FIFO)
  - `PATCH /api/orders/{id}` - Update order status

- **Catalog API**:
  - `GET /api/pizza-types` - List available pizzas with pricing
