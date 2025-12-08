# NeoBank API

NeoBank API is a robust, RESTful transactional banking system built with Java 17 and Spring Boot 3. It simulates the core operations of a financial institution, focusing on data consistency, high concurrency management, and security compliance.

This project demonstrates the implementation of a secure financial backend capable of handling real-time transfers, account management, and audit logging within a containerized environment.

## Architectural Highlights

This project addresses specific challenges found in financial software development:

### 1. Concurrency Control & Data Consistency
To prevent race conditions during simultaneous fund transfers, the system implements **Pessimistic Locking** (`PESSIMISTIC_WRITE`) at the database level.
* **Mechanism:** The `AccountRepository` explicitly locks the rows involved in a transaction until the operation commits.
* **Result:** Ensures ACID compliance and prevents negative balances even under high concurrent load.

### 2. Security Layering
Security is implemented through a multi-layered approach:
* **Authentication:** Stateless authentication using JWT (JSON Web Tokens) with a custom security filter chain.
* **Authorization:** Role-Based Access Control (RBAC) segregating endpoints between `ADMIN` and `USER` roles.
* **DDoS Protection:** Application-level Rate Limiting implemented via **Bucket4j** (Token bucket algorithm), restricting traffic per IP address to prevent abuse.

### 3. Data Privacy (Encryption at Rest)
Sensitive personal information (PII), specifically IBANs, is encrypted before persistence using an implementation of `AttributeConverter`.
* **Algorithm:** AES encryption.
* **Behavior:** Data is stored encrypted in the database but decrypted automatically by the ORM when retrieved by the application context.

### 4. Auditing via AOP
Cross-cutting concerns such as audit logging are decoupled from business logic using **Aspect-Oriented Programming (Spring AOP)**.
* An `@Audit` annotation intercepts critical methods.
* Transaction details are asynchronously logged to an `audit_logs` table for compliance and tracking.

## Technology Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3.x
* **Security:** Spring Security 6, JWT, BCrypt, Bucket4j
* **Persistence:** Spring Data JPA, Hibernate, MySQL 8.0 (Production), H2 (Testing)
* **Reporting:** OpenPDF (Statement generation)
* **Testing:** JUnit 5, Mockito
* **Infrastructure:** Docker, Docker Compose

## Getting Started

The application is containerized for easy deployment.

### Prerequisites
* Docker & Docker Compose

### Installation & Execution

1. Clone the repository.
2. Build and start the services using Docker Compose:

docker-compose up -d --build

This command will:
* Build the Java application using a multi-stage Dockerfile (Maven build -> JRE runtime).
* Spin up a MySQL 8.0 container.
* Expose the API on port `8080`.

### Default Credentials

Upon initialization, the `DataInitializer` bean pre-loads the following users for testing purposes:

| Role | Email | Password |
| :--- | :--- | :--- |
| **ADMIN** | `admin@neobank.com` | `admin123` |
| **USER** | `andres@neobank.com` | `user123` |

## API Documentation

The API is fully documented using the OpenAPI 3.0 specification. Once the application is running, the Swagger UI is available at:

**URL:** `http://localhost:8080/swagger-ui/index.html`

*Note: Use the `/api/auth/login` endpoint to obtain a JWT. Authorize requests by clicking the "Authorize" button and entering the token as `Bearer <token>`.*

## Testing Strategy

The project emphasizes unit testing on the core business logic layer (`TransactionService`).

To execute the test suite:

mvn test

**Key Test Scenarios:**
* Successful money transfers between accounts.
* Prevention of self-transfers (same source and destination).
* Insufficient balance exception handling.
* Database lock verification (via Mockito verification).

## Project Structure

src/main/java/com/example/neo_bank/api
├── audit/          # AOP Aspects and Annotations
├── auth/           # Authentication Controllers (Login/Register)
├── config/         # Security, Swagger, and Data Initialization
├── controller/     # REST Controllers
├── dto/            # Data Transfer Objects
├── model/          # JPA Entities
├── ratelimit/      # Rate Limiting Logic
├── repository/     # Data Access Layer
├── scheduler/      # Scheduled Jobs (Interest calculation)
├── security/       # JWT Filters and UserDetails implementation
├── service/        # Business Logic
└── util/           # Encryption Utilities

## License

This project is open-source and available under the MIT License.
