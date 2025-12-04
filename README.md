# NeoBank API 

A production-ready Banking REST API simulation built with **Java 17** and **Spring Boot 3**.

This project demonstrates the implementation of a scalable financial backend. It focuses on data integrity, handling complex SQL relationships, and ensuring precision in financial calculations using `BigDecimal` and strict ACID compliance.

## Tech Stack

* **Core:** Java 17 (LTS), Spring Boot 3.2
* **Database:** MySQL 8.0 (Containerized)
* **DevOps:** Docker, Docker Compose
* **Data Access:** Spring Data JPA, Hibernate
* **Documentation:** OpenAPI / Swagger UI

## Key Features

* **Secure Transactions:** Implements `@Transactional` logic to ensure money transfers are atomic (ACID compliant). Includes rollback mechanisms for failed operations.
* **Data Validation:** Strict input validation to prevent invalid financial states (e.g., negative transfers, overdrafts).
* **Precision Logic:** Usage of `BigDecimal` for all monetary values to avoid floating-point rounding errors common in financial software.
* **Docker Ready:** Fully containerized environment. The database and the application can be orchestrated via `docker-compose`.
* **Audit History:** Comprehensive transaction tracking (Deposits and Transfers) stored in the database.

## Architecture

The project follows a **Layered Architecture** to ensure separation of concerns:

1.  **Controller Layer:** Handles HTTP requests and responses.
2.  **Service Layer:** Contains business logic (validations, calculations, transactional boundaries).
3.  **Repository Layer:** Interacts with the MySQL database via JPA.
4.  **DTOs & Mappers:** Decouples the internal database entities from the external API representation.

## 🏁 Getting Started

### Prerequisites
* Docker & Docker Compose
* Java 17 SDK
* Maven

### Run with Docker

You can spin up the MySQL database and the API with a single command:

```bash
docker-compose up -d
API Documentation (Swagger UI)
This API includes an interactive documentation using OpenAPI / Swagger. You can test endpoints directly from your browser without installing Postman.

Open Swagger UI
https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html
(Note: The application must be running locally for this link to work)
