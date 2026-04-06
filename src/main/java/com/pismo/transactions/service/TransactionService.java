package com.pismo.transactions.service;

import com.pismo.transactions.model.Transaction;
import com.pismo.transactions.repository.AccountRepository;
import com.pismo.transactions.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    // Operation type constants
    private static final Long OP_COMPRA_A_VISTA = 1L;
    private static final Long OP_COMPRA_PARCELADA = 2L;
    private static final Long OP_SAQUE = 3L;
    private static final Long OP_PAGAMENTO = 4L;

    private static final Set<Long> VALID_OPERATION_TYPES = Set.of(
            OP_COMPRA_A_VISTA,
            OP_COMPRA_PARCELADA,
            OP_SAQUE,
            OP_PAGAMENTO
    );

    private static final Set<Long> DEBIT_OPERATION_TYPES = Set.of(
            OP_COMPRA_A_VISTA,
            OP_COMPRA_PARCELADA,
            OP_SAQUE
    );

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        // Validate account exists
        if (transaction.getAccountId() == null) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Validate amount is positive
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount should be positive");
        }

        // Validate operation type id
        Long operationTypeId = transaction.getOperationTypeId();
        if (operationTypeId == null || !VALID_OPERATION_TYPES.contains(operationTypeId)) {
            throw new RuntimeException("Operation type not found");
        }

        // Debit operations (purchases, withdrawals) are stored as negative amounts
        if (DEBIT_OPERATION_TYPES.contains(operationTypeId)) {
            transaction.setAmount(transaction.getAmount().negate());
        }

        // Set event date
        transaction.setEventDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccount(UUID accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findByAccountId(accountId);
    }
}
