# LedgerLine - Development Prompts Log

This document records all the prompts provided for the development of **LedgerLine – Secure Financial Transaction Management System** in chronological order.

---

### Prompt 1: Project Kickoff & Initial Workspace Analysis

```text
I am developing a project called **LedgerLine – Secure Financial Transaction Management System**.

### Technology Stack
- Frontend: React + Vite
- Backend: Java 21 + Spring Boot
- Build Tool: Maven
- Database: PostgreSQL
- ORM: Spring Data JPA / Hibernate
- Security: Spring Security
- API Communication: REST APIs using JSON

### Development Rules
1. I am learning Spring Boot, so explain everything in beginner-friendly language.
2. Follow a step-by-step development approach.
3. Do not generate the entire application at once.
4. Explain the purpose of every file and class before providing code.
5. Follow a clean layered architecture:
   - Controller
   - Service
   - Repository
   - Entity
   - DTO
   - Configuration
6. Use proper exception handling and input validation.
7. Never trust frontend validation for financial operations.
8. Use BigDecimal for all monetary calculations.
9. Use PostgreSQL NUMERIC for storing monetary amounts.
10. Use database transactions, concurrency protection and idempotency wherever required.
11. Follow secure coding practices and never expose passwords or secrets.
12. Do not modify existing files unnecessarily.
13. Before creating a new file, inspect the existing project structure.
14. Provide complete code for each requested file, including its exact path.
15. Give Windows CMD commands when terminal commands are needed.
16. After each feature, explain how to test it using Postman or the frontend.
17. Never claim a feature is implemented or tested unless it has actually been verified.

### Project Objective
Build a reliable financial transaction system supporting administrators, account holders and auditors, with secure authentication, account management, deposits, withdrawals, transfers, double-entry bookkeeping, idempotency, concurrency protection and audit logging.

Start by understanding my existing project. Do not generate code until I request the next implementation step.
analyze anf install react and what ever necessary
```

---

### Prompt 2: Phase 1 Specification (Backend Foundation)

```text
We are starting Phase 1 of LedgerLine: Backend Foundation.

My existing environment:
- Java 21
- Spring Boot
- Maven
- PostgreSQL
- Spring Data JPA
- React + Vite frontend

Tasks:

1. Inspect my existing backend project structure.
2. Explain the purpose of pom.xml, application.properties, the main application class and existing configuration files.
3. Verify the PostgreSQL database connection.
4. Configure Spring Data JPA and Hibernate properly.
5. Organise the backend into these packages:
   - config
   - controller
   - service
   - repository
   - entity
   - dto
   - exception
   - security
6. Configure environment variables for database credentials.
7. Configure appropriate application profiles for development.
8. Verify the existing health-check endpoint.
9. Add a database connectivity health check.
10. Configure Flyway database migrations if compatible with the existing project.

Requirements:
- Do not remove existing working configurations without explaining why.
- Do not hardcode credentials.
- Do not create unnecessary classes.
- Use Java 21 compatible dependencies.
- Explain each configuration in beginner-friendly language.

Testing:
- Run Maven compilation.
- Run the Spring Boot application.
- Verify the health endpoint.
- Verify actual PostgreSQL connectivity.

First explain the existing structure and proposed changes. Implement only after I approve.
```

---


---

### Prompt 3: Phase 2 Specification (Authentication & Role-Based Authorisation)

```text
Implement Phase 2 of LedgerLine: Authentication and Role-Based Authorisation.

User roles:
1. ADMIN
2. ACCOUNT_HOLDER
3. AUDITOR

Requirements:

1. Create the User entity.
2. Create the Role entity or an appropriate role representation.
3. Implement user registration.
4. Implement secure password hashing using BCrypt.
5. Implement login authentication.
6. Implement Spring Security configuration.
7. Use JWT-based authentication with appropriate token validation and expiration.
8. Create authentication DTOs.
9. Implement secure logout/token invalidation if using a revocable token strategy.
10. Implement role-based endpoint protection.
11. Prevent users from accessing endpoints they are not authorised to access.
12. Ensure passwords and sensitive authentication data are never returned in API responses.

Access rules:
- ADMIN: Administrative operations.
- ACCOUNT_HOLDER: Own account and financial operations.
- AUDITOR: Read-only audit and transaction inspection.

Security requirements:
- Never trust role values sent by the frontend.
- Verify user identity on every protected request.
- Enforce authorisation on the backend.
- Use secure password handling.
- Return appropriate HTTP status codes.
- Prevent account enumeration and insecure registration.
- Do not expose JWT signing secrets.

Testing:
- Valid login.
- Invalid password.
- Duplicate registration.
- Expired or invalid JWT.
- Account holder accessing admin endpoint.
- Auditor attempting a financial operation.
- Unauthenticated access to protected endpoints.

Explain Spring Security filters, authentication, authorisation and JWT flow before implementation.

Implement one component at a time and wait for my approval before proceeding.
```

---

