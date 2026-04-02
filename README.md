# Transaction Routine Spring Boot Application

This is a Spring Boot application that implements a transaction routine system with accounts, operation types, and transactions.

## Features

- Account management (create and retrieve accounts)
- Transaction creation with automatic amount sign adjustment based on operation type
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

### Transactions
- `POST /transactions`: Create a new transaction
  - Request: `{"account_id": 1, "operation_type_id": 4, "amount": 123.45}`
  - Response: Created transaction object

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

## Testing the API

### Create an Account
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"document_number": "12345678900"}'
```

### Get Account
```bash
curl http://localhost:8080/accounts/1
```

### Create a Transaction
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{"account_id": 1, "operation_type_id": 4, "amount": 100.00}'
```

## Technologies Used

- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Docker
- Maven