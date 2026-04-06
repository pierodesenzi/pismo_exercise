package com.pismo.transactions.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "operation_types")
public class OperationType {
    @Id
    @JsonProperty("operation_type_id")
    private Long operationTypeId;

    @Column(nullable = false)
    private String description;

    public OperationType() {}

    public OperationType(Long operationTypeId, String description) {
        this.operationTypeId = operationTypeId;
        this.description = description;
    }

    public Long getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(Long operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}