package com.example.demo.service;

import com.example.demo.model.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private static final Set<Long> VALID_OPERATION_TYPES = Set.of(1L, 2L, 3L, 4L);

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        // Validate account exists
        accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Validate operation type id
        Long operationTypeId = transaction.getOperationTypeId();
        if (!VALID_OPERATION_TYPES.contains(operationTypeId)) {
            throw new RuntimeException("Operation type not found");
        }

        // Adjust amount sign based on operation type
        Double adjustedAmount = transaction.getAmount();
        if (operationTypeId == 1L || operationTypeId == 2L || operationTypeId == 3L) {
            adjustedAmount = -Math.abs(adjustedAmount);
        } else {
            adjustedAmount = Math.abs(adjustedAmount);
        }
        transaction.setAmount(adjustedAmount);

        // Set event date
        transaction.setEventDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findAll().stream()
                .filter(t -> t.getAccountId().equals(accountId))
                .toList();
    }
}
