package com.pismo.transactions;

import com.pismo.transactions.model.Account;
import com.pismo.transactions.model.Transaction;
import com.pismo.transactions.repository.AccountRepository;
import com.pismo.transactions.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
                .andExpect(jsonPath("$.amount", is(200.0)))
                .andExpect(jsonPath("$.transaction_id", notNullValue()))
                .andExpect(jsonPath("$.event_date", notNullValue()));

        mockMvc.perform(get("/transactions/account/{accountId}", account.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].account_id", is(account.getAccountId().toString())));
    }

    @Test
    void shouldStoreNegativeAmountForPurchase() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setOperationTypeId(1L); // COMPRA_A_VISTA
        transaction.setAmount(150.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(-150.0)));
    }

    @Test
    void shouldStoreNegativeAmountForInstallmentPurchase() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setOperationTypeId(2L); // COMPRA_PARCELADA
        transaction.setAmount(300.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(-300.0)));
    }

    @Test
    void shouldStoreNegativeAmountForWithdrawal() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setOperationTypeId(3L); // SAQUE
        transaction.setAmount(50.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(-50.0)));
    }

    @Test
    void shouldStorePositiveAmountForPayment() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setOperationTypeId(4L); // PAGAMENTO
        transaction.setAmount(500.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(500.0)));
    }

    @Test
    void shouldRejectNegativeAmount() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setOperationTypeId(4L);
        transaction.setAmount(-100.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error", is("Transaction amount should be positive")));
    }

    @Test
    void shouldRejectZeroAmount() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setOperationTypeId(4L);
        transaction.setAmount(0.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error", is("Transaction amount should be positive")));
    }

    @Test
    void shouldReturn500ForInvalidOperationType() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setOperationTypeId(99L); // invalid
        transaction.setAmount(100.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void shouldFailWhenAccountNotFound() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(UUID.randomUUID()); // Non-existent account
        transaction.setOperationTypeId(4L);
        transaction.setAmount(300.0);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void shouldReturnEmptyListForAccountWithNoTransactions() throws Exception {
        mockMvc.perform(get("/transactions/account/{accountId}", account.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnAllTransactionsForAccount() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Transaction t = new Transaction();
            t.setAccountId(account.getAccountId());
            t.setOperationTypeId(4L);
            t.setAmount(i * 100.0);
            mockMvc.perform(post("/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(t)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/transactions/account/{accountId}", account.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
