# NeoBank API

A production-ready Banking REST API simulation built with **Java 17** and **Spring Boot 3**.

This project demonstrates the implementation of a scalable financial backend with **Bank-Grade Security**. It focuses on data integrity, complex relationships (Users-Accounts-Cards), and secure access control.

## Tech Stack

* **Core:** Java 17 (LTS), Spring Boot 3.2
* **Security:** Spring Security 6, JWT (JSON Web Tokens)
* **Database:** MySQL 8.0 (Containerized)
* **DevOps:** Docker, Docker Compose (Multi-stage build)
* **Documentation:** OpenAPI / Swagger UI

## Key Features

### Security & Access Control
* **JWT Authentication:** Stateless authentication using Bearer Tokens.
* **Role-Based Access Control (RBAC):**
    * `ADMIN`: Can audit all users, accounts, and global transactions.
    * `USER`: Can only access their own data and assets.
* **Data Masking:** Sensitive data (like card numbers) is masked in API responses (`**** **** **** 1234`).

### Financial Core
* **Atomic Transactions:** Uses `@Transactional` to ensure money transfers are ACID compliant.
* **Precision Logic:** `BigDecimal` for all monetary calculations to prevent rounding errors.
* **Card Management:** System to issue Debit/Credit cards with auto-generated secure numbers and CVVs.

### Architecture
The project follows a strict **Layered Architecture**:
1.  **Web Layer:** REST Controllers with DTO validation.
2.  **Service Layer:** Business logic and transactional boundaries.
3.  **Persistence Layer:** JPA Repositories interacting with MySQL.
4.  **Security Layer:** Custom Filters and Authentication Providers.

---

## Getting Started (The Easy Way)

You don't need Java installed. Just **Docker**.

### 1. Run the project
```bash
docker-compose up -d --build
2. Access the System
Swagger UI (API Docs): http://localhost:8080/swagger-ui/index.html
Testing Credentials
The system comes with Roles pre-configured. You can register new users via Swagger or use these roles logic:
Role Capabilities Endpoint Access 
USER Transfer money, View own Account, Request Cards GET /api/accounts/{myId}
ADMIN View ALL Users, View ALL Accounts, Global AuditGET /api/users, GET /api/transactions 
API Endpoints Overview
Method Endpoint Description Auth Required
POST /api/auth/register Register new user (User/Admin) Public 
POST/api/auth/loginGet JWT Token Public
POST/api/transactions Transfer money Token
GET/api/cards/{accountId} View Cards (Masked) Token (Owner)
GET/api/users List all customers Admin Only
Author: Pablo Segura Martos Built with Java.
