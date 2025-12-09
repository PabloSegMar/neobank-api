# NeoBank API - Professional Fintech Backend

NeoBank API is a production-ready, RESTful transactional banking system built with Java 17 and Spring Boot 3. It simulates the core operations of a financial institution, designed as a robust "Starter Kit" for high-concurrency financial applications.

This project focuses on Data Consistency, Security, Observability, and Developer Experience.

## Key Features & Architecture

This project addresses specific complex backend challenges found in the Fintech industry:

### 1. Concurrency Control (ACID)
To prevent race conditions during simultaneous fund transfers, the system implements Pessimistic Locking (PESSIMISTIC_WRITE) at the database level.
* Mechanism: The AccountRepository explicitly locks the rows involved in a transaction until the operation commits.
* Result: Ensures ACID compliance and prevents negative balances even under high concurrent load.

### 2. Advanced Security
Security is implemented through a multi-layered approach:
* Authentication: Stateless authentication using JWT (JSON Web Tokens) with a custom security filter chain.
* Authorization: Role-Based Access Control (RBAC) segregating endpoints between ADMIN and USER roles.
* DDoS Protection: Application-level Rate Limiting implemented via Bucket4j (Token bucket algorithm), restricting traffic per IP address to prevent abuse.
* Data Privacy: Sensitive personal information (PII), specifically IBANs, is encrypted before persistence using an implementation of AttributeConverter (AES algorithm). Data is stored encrypted in the database but decrypted automatically by the ORM when retrieved.

### 3. Observability & Logging
The system implements a professional logging strategy suitable for production environments:
* Traceability: Every HTTP request is intercepted and assigned a unique Trace ID ([req-xxxxx]) via MDC (Mapped Diagnostic Context), allowing for complete request tracking across logs.
* Audit Trail: Critical actions are intercepted via Spring AOP and asynchronously logged to an audit_logs table for compliance and tracking.

### 4. Robust Error Handling
Errors are managed through a centralized Global Exception Handler (@RestControllerAdvice).
* Behavior: Replaces standard server errors (500) with clean, standardized JSON responses (HTTP 400/404) that include timestamps and user-friendly messages, avoiding the exposure of stack traces.

### 5. Decoupled Notifications
The architecture includes an asynchronous notification system.
* Design: The TransactionService is decoupled from the notification logic via interfaces.
* Implementation: currently simulates email sending (console output with latency simulation), designed to be easily swapped for an external provider like SendGrid or SMTP without modifying business logic.

## Technology Stack

* Language: Java 17
* Framework: Spring Boot 3.3
* Security: Spring Security, JWT, BCrypt, Bucket4j
* Persistence: Spring Data JPA, Hibernate, MySQL 8.0
* Observability: Slf4j, MDC Tracing
* Reporting: OpenPDF (Statement generation)
* Documentation & Testing: OpenAPI (Swagger), Postman, JUnit 5, Mockito
* Infrastructure: Docker, Docker Compose

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
* Spin up a MySQL 8.0 container with a persistent volume.
* Expose the API on port 8080.

## Usage & Testing

### Option A: Postman (Recommended)
This repository includes a Full Postman Collection (neobank_collection.json) located in the root folder.
1. Open Postman.
2. Click "Import" and upload the file.
3. Use the pre-configured requests to Login, Transfer Money, and view Audits.

### Option B: Swagger UI
Interactive API documentation is available once the application is running:
URL: http://localhost:8080/swagger-ui/index.html

### Option C: Default Credentials
On the first run, the DataInitializer creates sample users. You can verify their generated IBANs in the console logs (docker logs -f neobank_api).

* Admin: admin@neobank.com / admin123 (or your existing password if data persists)
* User: andres@neobank.com / user123

## Configuration

Business rules are externalized in src/main/resources/application.properties:

# Business Logic Configuration
neobank.business.daily-limit=2000.00
neobank.business.interest-rate=0.01

## Project Structure

src/main/java/com/example/neo_bank/api
├── audit/          # AOP Aspects for audit logging
├── auth/           # Login/Register Controllers
├── config/         # Security, Swagger, CORS, DataInit
├── controller/     # REST Controllers
├── dto/            # Data Transfer Objects (Requests/Responses)
├── exception/      # Global Exception Handler
├── model/          # JPA Entities (User, Account, Transaction)
├── notification/   # Notification System (Interfaces/Impl)
├── pdf/            # PDF Generation Service
├── ratelimit/      # Anti-DDoS Logic
├── repository/     # Database Access
├── scheduler/      # Cron Jobs (Monthly Interests)
├── security/       # JWT Logic & Filters
├── service/        # Core Business Logic (Transactional)
└── util/           # Encryption Utilities

## Author

Developed by Pablo Segura Martos

## License

This project is open-source and available under the MIT License.
