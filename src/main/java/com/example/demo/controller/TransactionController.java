package com.example.demo.controller;

import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }

    @GetMapping("/account/{accountId}")
    public List<Transaction> getTransactionsByAccount(@PathVariable("accountId") Long accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException e) {
        return e.getMessage();
    }
}