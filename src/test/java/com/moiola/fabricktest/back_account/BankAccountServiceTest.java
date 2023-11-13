package com.moiola.fabricktest.back_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moiola.fabricktest.bank_account.BankAccountServiceImpl;
import com.moiola.fabricktest.bank_account.FabrickResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BankAccountServiceImpl.class)
public class BankAccountServiceTest {

    private static final long accountId = 14537780;
    private static final String apiKey = "AAA";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Mock
    private RestTemplate restTemplateMock;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;


    @Test
    public void testGetBalanceResponse() throws Exception {
        ResponseEntity<FabrickResponse> mockResponseEntity = new ResponseEntity<>(getBalanceResponseExample(), HttpStatus.OK);

        Mockito.when(restTemplateMock.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(FabrickResponse.class)
        )).thenReturn(mockResponseEntity);

        JsonNode actualBalance = bankAccountService.getBalance(accountId, apiKey);
        JsonNode expectedBalance = getBalanceExample();

        assertEquals(expectedBalance.asText(), actualBalance.asText());
    }

    @Test
    public void testGetTransactionsResponse() throws Exception {
        ResponseEntity<FabrickResponse> mockResponseEntity = new ResponseEntity<>(getTransactionsResponseExample(), HttpStatus.OK);

        Mockito.when(restTemplateMock.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(FabrickResponse.class)
        )).thenReturn(mockResponseEntity);

        JsonNode actualTransactions = bankAccountService.getTransactions(accountId, apiKey, dateFormat.parse("2019-01-01"),dateFormat.parse("2019-12-01"));
        JsonNode expectedTransactions = getTransactionsExample();

        Assertions.assertEquals(actualTransactions.asText(), expectedTransactions.asText());
    }

    @Test
    public void testMakeTransferResponse() throws Exception {
        ResponseEntity<FabrickResponse> mockResponseEntity = new ResponseEntity<>(getResponseTransferExample(), HttpStatus.OK);

        Mockito.when(restTemplateMock.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FabrickResponse.class)
        )).thenReturn(mockResponseEntity);

        JsonNode actualTransferResponse = bankAccountService.makeTransfer(accountId, apiKey, getRequestBodyTransferExample());
        JsonNode expectedTransferResponse = getResponsePayloadTransferExample();

        Assertions.assertEquals(actualTransferResponse.asText(), expectedTransferResponse.asText());
    }


    private FabrickResponse getBalanceResponseExample() throws JsonProcessingException {
        return new FabrickResponse("OK", new ArrayList<>(),getBalanceExample());
    }

    private JsonNode getBalanceExample() throws JsonProcessingException {
        String payload = "{\n" +
                "        \"date\": \"2023-11-13\",\n" +
                "        \"balance\": -9.51,\n" +
                "        \"availableBalance\": -9.51,\n" +
                "        \"currency\": \"EUR\"\n" +
                "    }";
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(payload);
    }

    private FabrickResponse getTransactionsResponseExample() throws JsonProcessingException {
        return new FabrickResponse("OK", new ArrayList<>(),getTransactionsExample());
    }

    private JsonNode getTransactionsExample() throws JsonProcessingException {
        String payload = "\"list\": [\n" +
                "            {\n" +
                "                \"transactionId\": \"282831\",\n" +
                "                \"operationId\": \"00000000282831\",\n" +
                "                \"accountingDate\": \"2019-11-29\",\n" +
                "                \"valueDate\": \"2019-12-01\",\n" +
                "                \"type\": {\n" +
                "                    \"enumeration\": \"GBS_TRANSACTION_TYPE\",\n" +
                "                    \"value\": \"GBS_ACCOUNT_TRANSACTION_TYPE_0050\"\n" +
                "                },\n" +
                "                \"amount\": -343.77,\n" +
                "                \"currency\": \"EUR\",\n" +
                "                \"description\": \"PD VISA CORPORATE 10\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"transactionId\": \"1460159524001\",\n" +
                "                \"operationId\": \"19000191134336\",\n" +
                "                \"accountingDate\": \"2019-11-11\",\n" +
                "                \"valueDate\": \"2019-11-09\",\n" +
                "                \"type\": {\n" +
                "                    \"enumeration\": \"GBS_TRANSACTION_TYPE\",\n" +
                "                    \"value\": \"GBS_ACCOUNT_TRANSACTION_TYPE_0010\"\n" +
                "                },\n" +
                "                \"amount\": 854.00,\n" +
                "                \"currency\": \"EUR\",\n" +
                "                \"description\": \"BD LUCA TERRIBILE        DA 03268.49130         DATA ORDINE 09112019 COPERTURA VISA\"\n" +
                "            }]";
        return new ObjectMapper().readTree(payload);
    }

    private JsonNode getRequestBodyTransferExample() throws JsonProcessingException{
        String request = "{\n" +
                "  \"creditor\": {\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"account\": {\n" +
                "      \"accountCode\": \"IT23A0336844430152923804660\",\n" +
                "      \"bicCode\": \"SELBIT2BXXX\"\n" +
                "    },\n" +
                "    \"address\": {\n" +
                "      \"address\": null,\n" +
                "      \"city\": null,\n" +
                "      \"countryCode\": null\n" +
                "    }\n" +
                "  },\n" +
                "  \"executionDate\": \"2019-04-01\",\n" +
                "  \"uri\": \"REMITTANCE_INFORMATION\",\n" +
                "  \"description\": \"Payment invoice 75/2017\",\n" +
                "  \"amount\": 800,\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"isUrgent\": false,\n" +
                "  \"isInstant\": false,\n" +
                "  \"feeType\": \"SHA\",\n" +
                "  \"feeAccountId\": \"45685475\",\n" +
                "  \"taxRelief\": {\n" +
                "    \"taxReliefId\": \"L449\",\n" +
                "    \"isCondoUpgrade\": false,\n" +
                "    \"creditorFiscalCode\": \"56258745832\",\n" +
                "    \"beneficiaryType\": \"NATURAL_PERSON\",\n" +
                "    \"naturalPersonBeneficiary\": {\n" +
                "      \"fiscalCode1\": \"MRLFNC81L04A859L\",\n" +
                "      \"fiscalCode2\": null,\n" +
                "      \"fiscalCode3\": null,\n" +
                "      \"fiscalCode4\": null,\n" +
                "      \"fiscalCode5\": null\n" +
                "    },\n" +
                "    \"legalPersonBeneficiary\": {\n" +
                "      \"fiscalCode\": null,\n" +
                "      \"legalRepresentativeFiscalCode\": null\n" +
                "    }\n" +
                "  }\n}";
        return new ObjectMapper().readTree(request);
    }

    private FabrickResponse getResponseTransferExample() throws JsonProcessingException{
        return new FabrickResponse("OK",new ArrayList<>(),getResponsePayloadTransferExample());
    }

    private JsonNode getResponsePayloadTransferExample() throws JsonProcessingException {
        String responseBody = "{\n" +
                "  \"moneyTransferId\": \"452516859427\",\n" +
                "  \"status\": \"EXECUTED\",\n" +
                "  \"direction\": \"OUTGOING\",\n" +
                "  \"creditor\": {\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"account\": {\n" +
                "      \"accountCode\": \"IT23A0336844430152923804660\",\n" +
                "      \"bicCode\": \"SELBIT2BXXX\"\n" +
                "    },\n" +
                "    \"address\": {\n" +
                "      \"address\": null,\n" +
                "      \"city\": null,\n" +
                "      \"countryCode\": null\n" +
                "    }\n" +
                "  },\n" +
                "  \"debtor\": {\n" +
                "    \"name\": \"\",\n" +
                "    \"account\": {\n" +
                "      \"accountCode\": \"IT61F0326802230280596327270\",\n" +
                "      \"bicCode\": null\n" +
                "    }\n" +
                "  },\n" +
                "  \"cro\": \"1234566788907\",\n" +
                "  \"uri\": \"REMITTANCE_INFORMATION\",\n" +
                "  \"trn\": \"AJFSAD1234566788907CCSFDGTGVGV\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"createdDatetime\": \"2019-04-10T10:38:55.949+0200\",\n" +
                "  \"accountedDatetime\": \"2019-04-10T10:38:56.000+0200\",\n" +
                "  \"debtorValueDate\": \"2019-04-10\",\n" +
                "  \"creditorValueDate\": \"2019-04-10\",\n" +
                "  \"amount\": {\n" +
                "    \"debtorAmount\": 800,\n" +
                "    \"debtorCurrency\": \"EUR\",\n" +
                "    \"creditorAmount\": 800,\n" +
                "    \"creditorCurrency\": \"EUR\",\n" +
                "    \"creditorCurrencyDate\": \"2019-04-10\",\n" +
                "    \"exchangeRate\": 1\n" +
                "  },\n" +
                "  \"isUrgent\": false,\n" +
                "  \"isInstant\": false,\n" +
                "  \"feeType\": \"SHA\",\n" +
                "  \"feeAccountId\": \"12345678\",\n" +
                "  \"fees\": [\n" +
                "    {\n" +
                "      \"feeCode\": \"MK001\",\n" +
                "      \"description\": \"Money transfer execution fee\",\n" +
                "      \"amount\": 0.25,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"feeCode\": \"MK003\",\n" +
                "      \"description\": \"Currency exchange fee\",\n" +
                "      \"amount\": 3.5,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"hasTaxRelief\": true\n" +
                "}";
        return new ObjectMapper().readTree(responseBody);
    }
}
