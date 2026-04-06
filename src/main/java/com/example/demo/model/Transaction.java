package com.example.pismo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("transaction_id")
    private UUID transactionId;

    @Column(name = "account_id", nullable = false)
    @JsonProperty("account_id")
    private UUID accountId;

    @Column(name = "operation_type_id", nullable = false)
    @JsonProperty("operation_type_id")
    private Long operationTypeId;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "event_date", nullable = false)
    @JsonProperty("event_date")
    private LocalDateTime eventDate;

    public Transaction() {}

    public Transaction(UUID transactionId, UUID accountId, Long operationTypeId, Double amount, LocalDateTime eventDate) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.operationTypeId = operationTypeId;
        this.amount = amount;
        this.eventDate = eventDate;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public Long getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(Long operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}