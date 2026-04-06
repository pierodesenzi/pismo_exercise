package com.example.pismo;

import com.example.pismo.model.Account;
import com.example.pismo.repository.AccountRepository;
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
import static org.hamcrest.Matchers.notNullValue;
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
                .andExpect(jsonPath("$.account_id", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        Account saved = objectMapper.readValue(created, Account.class);

        mockMvc.perform(get("/accounts/{accountId}", saved.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id", is(saved.getAccountId().toString())))
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

    @Test
    void shouldReturnEmptyListWhenNoAccounts() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturn500WhenGettingNonExistentAccount() throws Exception {
        mockMvc.perform(get("/accounts/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void shouldReturn409WhenCreatingDuplicateAccount() throws Exception {
        Account account = new Account();
        account.setDocumentNumber("99999999999");

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk());

        // Second creation with same document_number
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("account already exists")));
    }

    @Test
    void shouldReturnUUIDAsAccountId() throws Exception {
        Account account = new Account();
        account.setDocumentNumber("55566677788");

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                // UUID format: 8-4-4-4-12 hex chars separated by hyphens
                .andExpect(jsonPath("$.account_id",
                        org.hamcrest.Matchers.matchesPattern(
                                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")));
    }
}
