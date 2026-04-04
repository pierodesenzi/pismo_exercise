# Transaction Routine Spring Boot Application

This is a Spring Boot application that implements a transaction routine system with accounts, operation types, and transactions.

## Features

- Account management (create, retrieve accounts, list all accounts)
- Transaction creation and retrieval by account
- Operation type management (retrieve operation types)
- Persistent storage using PostgreSQL
- Docker containerization

## Data Model

### Accounts
- `account_id`: Unique identifier
- `document_number`: Client's document number (CPF)

### Operation Types
- 1: COMPRA A VISTA (Cash Purchase)
- 2: COMPRA PARCELADA (Installment Purchase)
- 3: SAQUE (Withdrawal)
- 4: PAGAMENTO (Payment)

### Transactions
- `transaction_id`: Unique identifier
- `account_id`: Reference to account
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
  - Response: `{"account_id": 1, "document_number": "12345678900"}`

- `GET /accounts`: List all accounts
  - Response: Array of account objects

### Transactions
- `POST /transactions`: Create a new transaction
  - Request: `{"account_id": 1, "operation_type_id": 4, "amount": 123.45}`
  - Response: Created transaction object

- `GET /transactions/account/{accountId}`: Get transactions for an account
  - Response: Array of transaction objects

## Business Rules

- Purchase and withdrawal transactions are stored with negative amounts
- Payment transactions are stored with positive amounts
- All transactions are validated against existing accounts and operation types

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

## Running with Docker

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Start the services:
   ```bash
   docker-compose up --build
   ```

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

- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Docker
- Maven