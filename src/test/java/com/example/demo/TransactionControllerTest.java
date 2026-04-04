package com.example.demo;

import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Account account;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        account = accountRepository.save(new Account(null, "12345678900"));
    }

    @Test
    void shouldCreateTransactionAndRetrieveByAccount() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setOperationTypeId(4L);
        transaction.setAmount(200.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(200.0)));

        mockMvc.perform(get("/transactions/account/{accountId}", account.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].account_id", is(account.getAccountId().intValue())));
    }

    @Test
    void shouldFailWhenAccountNotFound() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(999L);
        transaction.setOperationTypeId(4L);
        transaction.setAmount(300.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isInternalServerError());
    }
}
