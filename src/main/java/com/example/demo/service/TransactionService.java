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
        if (isDebitOperation(operationTypeId)) {
            adjustedAmount = -Math.abs(adjustedAmount);
        } else {
            adjustedAmount = Math.abs(adjustedAmount);
        }
        transaction.setAmount(adjustedAmount);

        // Set event date
        transaction.setEventDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    /**
     * Checks if the operation type is a debit operation (debits account balance).
     *
     * @param operationTypeId the operation type ID
     * @return true if operation is debit (purchase, withdrawal); false if credit (payment)
     */
    private boolean isDebitOperation(Long operationTypeId) {
        return operationTypeId.equals(OP_COMPRA_A_VISTA) ||
               operationTypeId.equals(OP_COMPRA_PARCELADA) ||
               operationTypeId.equals(OP_SAQUE);
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findByAccountId(accountId);
    }
}}
