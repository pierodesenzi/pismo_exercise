package com.example.pismo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("account_id")
    private UUID accountId;

    @Column(name = "document_number", nullable = false, unique = true)
    @JsonProperty("document_number")
    private String documentNumber;

    public Account() {}

    public Account(UUID accountId, String documentNumber) {
        this.accountId = accountId;
        this.documentNumber = documentNumber;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}