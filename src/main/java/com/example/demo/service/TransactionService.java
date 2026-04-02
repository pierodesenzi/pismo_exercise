package com.example.demo.service;

import com.example.demo.model.OperationType;
import com.example.demo.model.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.OperationTypeRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final OperationTypeRepository operationTypeRepository;

    public TransactionService(TransactionRepository transactionRepository,
                             AccountRepository accountRepository,
                             OperationTypeRepository operationTypeRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.operationTypeRepository = operationTypeRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        // Validate account exists
        accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Validate operation type exists
        OperationType operationType = operationTypeRepository.findById(transaction.getOperationTypeId())
                .orElseThrow(() -> new RuntimeException("Operation type not found"));

        // Adjust amount sign based on operation type
        Double adjustedAmount = transaction.getAmount();
        if (operationType.getOperationTypeId() == 1L || operationType.getOperationTypeId() == 2L || operationType.getOperationTypeId() == 3L) {
            // COMPRA A VISTA, COMPRA PARCELADA, SAQUE -> negative
            adjustedAmount = -Math.abs(adjustedAmount);
        } else if (operationType.getOperationTypeId() == 4L) {
            // PAGAMENTO -> positive
            adjustedAmount = Math.abs(adjustedAmount);
        }
        transaction.setAmount(adjustedAmount);

        // Set event date
        transaction.setEventDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}