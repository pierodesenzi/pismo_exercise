package com.example.demo;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();
    }

    @Test
    void shouldCreateAndGetAccount() throws Exception {
        Account account = new Account();
        account.setDocumentNumber("12345678900");

        String created = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document_number", is("12345678900")))
                .andReturn().getResponse().getContentAsString();

        Account saved = objectMapper.readValue(created, Account.class);

        mockMvc.perform(get("/accounts/{accountId}", saved.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document_number", is("12345678900")));
    }

    @Test
    void shouldListAllAccounts() throws Exception {
        Account account1 = new Account();
        account1.setDocumentNumber("11111111111");
        Account account2 = new Account();
        account2.setDocumentNumber("22222222222");

        accountRepository.save(account1);
        accountRepository.save(account2);

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
