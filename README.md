# neobank-api
A robust Fintech REST API built with Spring Boot 3 and Docker. Features secure JWT authentication, ACID transaction management, and MySQL persistence
# NeoBank API 

A production-ready Banking REST API simulation built with **Java 17** and **Spring Boot 3**.

This project demonstrates the implementation of a secure, scalable financial backend. It focuses on data integrity, handling complex SQL relationships, and ensuring precision in financial calculations using `BigDecimal` and strict ACID compliance.

## Tech Stack

* **Core:** Java 17 (LTS), Spring Boot 3.2
* **Database:** MySQL 8.0 (Containerized)
* **Security:** Spring Security, JWT (JSON Web Tokens), BCrypt
* **DevOps:** Docker, Docker Compose
* **Data Access:** Spring Data JPA, Hibernate
* **Documentation:** OpenAPI / Swagger UI
* **Testing:** JUnit 5, Mockito

## Key Features

* ** Secure Transactions:** Implements `@Transactional` logic to ensure money transfers are atomic (ACID compliant). Includes rollback mechanisms for failed operations.
* ** JWT Authentication:** Stateless security model using JSON Web Tokens for secure API access.
* ** Data Validation:** Strict input validation to prevent invalid financial states (e.g., negative transfers, overdrafts).
* ** Precision Logic:** Usage of `BigDecimal` for all monetary values to avoid floating-point rounding errors common in financial software.
* ** Docker Ready:** Fully containerized environment. The database and the application can be orchestrated via `docker-compose`.

##  Architecture

The project follows a **Layered Architecture** to ensure separation of concerns:

1.  **Controller Layer:** Handles HTTP requests and responses.
2.  **Service Layer:** Contains business logic (validations, calculations, transactional boundaries).
3.  **Repository Layer:** Interacts with the MySQL database via JPA.
4.  **DTOs & Mappers:** Decouples the internal database entities from the external API representation.

## 🏁 Getting Started

### Prerequisites
* Docker & Docker Compose
* Java 17 SDK (optional if running locally without Docker)
* Maven

### Run with Docker (Recommended)

You can spin up the MySQL database and the API with a single command:

```bash
docker-compose up -d
