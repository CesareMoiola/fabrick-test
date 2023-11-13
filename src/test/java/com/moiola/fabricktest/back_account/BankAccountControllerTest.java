package com.moiola.fabricktest.back_account;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moiola.fabricktest.bank_account.BankAccountController;
import com.moiola.fabricktest.bank_account.BankAccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankAccountController.class)
public class BankAccountControllerTest {

    private static final String GET_BALANCE_URL = "/api/v1/accounts/%s/balance";
    private static final String GET_TRANSACTIONS_URL = "/api/v1/accounts/%s/transactions";
    private static final String MAKE_TRANSFER_URL = "/api/v1/accounts/%s/transfer";
    private static final long accountId = 14537780;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BankAccountService service;


    @Test
    void testGetBalanceShouldReturn400() throws Exception {

        //Account ID wrong
        mockMvc.perform(get(String.format(GET_BALANCE_URL, "14537780A"))
            .header("Api-Key","AAA")
        )
                .andExpect(status().isBadRequest());

        //Missing Api-Key
        mockMvc.perform(get(String.format(GET_BALANCE_URL, accountId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBalanceShouldReturn200() throws Exception {
        String apiKey = "AAA";
        URI url = new URI(String.format(GET_BALANCE_URL, accountId));
        JsonNode balance = getBalanceExample();

        Mockito.when(service.getBalance(accountId, apiKey)).thenReturn(balance);

        mockMvc.perform(get(url).header("Api-Key",apiKey)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(balance.toString()));
    }

    @Test
    void testGetBalanceShouldReturn500() throws Exception {
        String apiKey = "AAA";
        URI url = new URI(String.format(GET_BALANCE_URL, accountId));

        Mockito.when(service.getBalance(accountId, apiKey)).thenThrow(Exception.class);

        mockMvc.perform(get(url).header("Api-Key",apiKey))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetTransactionsShouldReturn400() throws Exception {

        //Account ID wrong
        mockMvc.perform(get(String.format(GET_TRANSACTIONS_URL, "14537780A"))
                        .header("Api-Key","AAA")
                        .param("fromAccountingDate", "2019-01-01")
                        .param("toAccountingDate", "2019-12-01")
                )
                .andExpect(status().isBadRequest());

        //Missing Api-Key
        mockMvc.perform(get(String.format(GET_TRANSACTIONS_URL, accountId))
                        .param("fromAccountingDate", "2019-01-01")
                        .param("toAccountingDate", "2019-12-01")
                )
                .andExpect(status().isBadRequest());

        //Format date wrong
        mockMvc.perform(get(String.format(GET_TRANSACTIONS_URL, accountId))
                        .header("Api-Key","AAA")
                        .param("fromAccountingDate", "2019/01/01")
                        .param("toAccountingDate", "2019/01/01")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTransactionsShouldReturn200() throws Exception {
        mockMvc.perform(get(String.format(GET_TRANSACTIONS_URL, accountId))
                        .header("Api-Key","AAA")
                        .param("fromAccountingDate", "2019-01-01")
                        .param("toAccountingDate", "2019-01-01")
                )
                .andExpect(status().isOk());
    }

    @Test
    void testGetTransactionsShouldReturn500() throws Exception {
        String apiKey = "AAA";
        URI url = new URI(String.format(GET_TRANSACTIONS_URL, accountId));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromAccountingDate = dateFormat.parse("2019-01-01");
        Date toAccountingDate = dateFormat.parse("2019-12-01");

        Mockito.when(service.getTransactions(accountId, apiKey, fromAccountingDate, toAccountingDate))
                .thenThrow(Exception.class);

        mockMvc.perform(get(url)
                        .header("Api-Key","AAA")
                        .param("fromAccountingDate", "2019-01-01")
                        .param("toAccountingDate", "2019-12-01")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testMakeTransferShouldReturn400() throws Exception {

        String requestBody = "{\"key\": \"value\"}";

        //Account ID wrong
        mockMvc.perform(post(String.format(MAKE_TRANSFER_URL, "14537780A"))
                        .header("Api-Key","AAA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        //Missing Api-Key
        mockMvc.perform(post(String.format(MAKE_TRANSFER_URL, accountId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        //Format body is wrong
        mockMvc.perform(post(String.format(MAKE_TRANSFER_URL, accountId))
                        .header("Api-Key","AAA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\": \"value\"")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMakeTransferShouldReturn200() throws Exception {

        String requestBody = "{\"key\": \"value\"}";

        mockMvc.perform(post(String.format(MAKE_TRANSFER_URL, accountId))
                        .header("Api-Key","AAA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());
    }

    @Test
    void testMakeTransferShouldReturn500() throws Exception {
        String apiKey = "AAA";
        String requestBody = "{\"key\": \"value\"}";
        URI url = new URI(String.format(MAKE_TRANSFER_URL, accountId));


        Mockito.when(service.makeTransfer(accountId, apiKey, new ObjectMapper().readTree(requestBody)))
                .thenThrow(Exception.class);

        mockMvc.perform(post(url)
                        .header("Api-Key","AAA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isInternalServerError());
    }

    private JsonNode getBalanceExample(){
        return objectMapper.createObjectNode()
                .put("date", "2018-08-17")
                .put("balance", 29.64)
                .put("availableBalance", 29.64)
                .put("currency", "EUR");
    }
}
