# 🏦 NeoBank API

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
2. Access the SystemSwagger UI (API Docs): http://localhost:8080/swagger-ui/index.htmlWeb Dashboard: http://localhost:8080 Testing CredentialsThe system comes with Roles pre-configured. You can register new users via Swagger or use these roles logic:RoleCapabilitiesEndpoint AccessUSERTransfer money, View own Account, Request CardsGET /api/accounts/{myId}ADMINView ALL Users, View ALL Accounts, Global AuditGET /api/users, GET /api/transactions API Endpoints OverviewMethodEndpointDescriptionAuth RequiredPOST/api/auth/registerRegister new user (User/Admin) PublicPOST/api/auth/loginGet JWT Token PublicPOST/api/transactionsTransfer money TokenGET/api/cards/{accountId}View Cards (Masked) Token (Owner)GET/api/usersList all customers Admin Only
