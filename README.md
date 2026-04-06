# Transaction Spring Boot Application
### Piero Acosta Desenzi

This is a Spring Boot application that implements a transaction routine system with accounts, operation types, and transactions.

## Features

- Account management (create, retrieve accounts, list all accounts)
- Transaction creation and retrieval by account
- Persistent storage using PostgreSQL
- Docker containerization

## Data Model

### Accounts
- `account_id`: UUID unique identifier (auto-generated)
- `document_number`: Client's document number

### Operation Types
- 1: COMPRA_A_VISTA (Cash Purchase)
- 2: COMPRA_PARCELADA (Installment Purchase)
- 3: SAQUE (Withdrawal)
- 4: PAGAMENTO (Payment)

### Transactions
- `transaction_id`: UUID unique identifier (auto-generated)
- `account_id`: Reference to account (UUID)
- `operation_type_id`: Reference to operation type
- `amount`: Transaction amount (negative for purchases/withdrawals, positive for payments)
- `event_date`: Timestamp of transaction

## API Endpoints

### Health Check
- `GET /health`: Check application health
  - Response: `{"status": "UP"}`

### Accounts
- `POST /accounts`: Create a new account
  - Request: `{"document_number": "12345678900"}`
  - Response: Account object with `account_id`

- `GET /accounts/{accountId}`: Get account information
  - Response: `{"account_id": "550e8400-e29b-41d4-a716-446655440000", "document_number": "12345678900"}`

- `GET /accounts`: List all accounts
  - Response: Array of account objects

### Transactions
- `POST /transactions`: Create a new transaction
  - Request: `{"account_id": "550e8400-e29b-41d4-a716-446655440000", "operation_type_id": 4, "amount": 123.45}`
  - Response: Created transaction object

- `GET /transactions/account/{accountId}`: Get transactions for an account
  - Response: Array of transaction objects

## Business Rules

- Purchase and withdrawal transactions are stored with negative amounts
- Payment transactions are stored with positive amounts
- All transactions are validated against existing accounts and operation types
- When registering a transaction, the `amount` in `POST /transactions` must always be a **strictly positive** number. Zero and negative values are rejected — the API derives the sign from the operation type, so accepting negative input would produce ambiguous payloads.

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

## API Documentation (Swagger/OpenAPI)

The API includes automatic Swagger UI documentation powered by SpringDoc OpenAPI. Once the application is running, access the documentation at:

- **Interactive Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON spec**: http://localhost:8080/api-docs
- **OpenAPI YAML spec**: http://localhost:8080/api-docs.yaml

The Swagger UI provides:
- Interactive endpoint exploration
- Request/response schema visualization
- Try-it-out functionality for all endpoints
- Error handling documentation

## Running with Docker

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Start the services:
   ```bash
   docker-compose up --build
   ```

3. Access the application:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/health

The application will be available at `http://localhost:8080`

## Running Locally

1. Start PostgreSQL database
2. Update `application.properties` with your database connection
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Testing

The application includes unit and integration tests using JUnit 5 and Spring Boot Test. Tests use an in-memory H2 database for isolation.

### Running Tests

```bash
mvn test
```

### Test Coverage

- `AccountControllerTest`: Tests account creation, retrieval, and listing
- `TransactionControllerTest`: Tests transaction creation and retrieval by account

## Security Considerations

### Unpredictable IDs

Sequential numeric IDs (1, 2, 3...) enable **IDOR (Insecure Direct Object Reference)** attacks — a malicious user who receives `account_id: 42` can trivially enumerate `/accounts/1`, `/accounts/2`, etc. to scrape all records. This API uses UUIDs (e.g. `550e8400-e29b-41d4-a716-446655440000`), which are cryptographically unpredictable and eliminate enumeration. Production systems should also enforce authorization checks so users can only access their own resources.

### Credentials

Database credentials (`postgres` / `password`) are hardcoded in `application.properties` and `docker-compose.yml` for simplicity — this project is an exercise meant to be easy to run locally. Production systems should never embed credentials in source code. Instead, use cluster secrets (e.g. Kubernetes Secrets, AWS Secrets Manager, HashiCorp Vault) and inject them as environment variables at runtime.

## Tech Stack

- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Docker
- Maven