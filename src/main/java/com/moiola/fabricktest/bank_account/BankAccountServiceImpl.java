package com.moiola.fabricktest.bank_account;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class BankAccountServiceImpl implements BankAccountService{

    private static final String BASE_URL = "https://sandbox.platfr.io";
    private static final String TRANSACTIONS_URI = "/api/gbs/banking/v4.0/accounts/%s/transactions";
    private static final String BALANCE_URI = "/api/gbs/banking/v4.0/accounts/%s/balance";
    private static final String TRANSFER_URI = "/api/gbs/banking/v4.0/accounts/%s/payments/money-transfers";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public JsonNode getBalance(long accountId, String apiKey) throws URISyntaxException {
        URI url = new URI(BASE_URL + String.format(BALANCE_URI, accountId));

        log.info("Get balance for account: " + accountId);
        log.info("Api request: " + url);

        ResponseEntity<FabrickResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                getHttpEntity(apiKey),
                FabrickResponse.class
        );

        FabrickResponse body = response.getBody();
        assert body != null : "Response body is null for request: '" + url + "'";
        return body.getPayload();
    }

    @Override
    public JsonNode getTransactions(long accountId, String apiKey, Date fromAccountingDate, Date toAccountingDate) throws Exception{

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        URI url = UriComponentsBuilder.fromUriString(BASE_URL + String.format(TRANSACTIONS_URI,accountId))
                .queryParam("fromAccountingDate", dateFormat.format(fromAccountingDate))
                .queryParam("toAccountingDate", dateFormat.format(toAccountingDate))
                .build()
                .toUri();

        log.info("Get transactions for account: " + accountId);
        log.info("Api request: " + url);

        ResponseEntity<FabrickResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                getHttpEntity(apiKey),
                FabrickResponse.class
        );

        FabrickResponse body = response.getBody();
        assert body != null : "Response body is null for request: '" + url + "'";
        return body.getPayload();
    }

    @Override
    public JsonNode makeTransfer(long accountId, String apiKey, JsonNode transfer) throws Exception {
        URI url = UriComponentsBuilder.fromUriString(BASE_URL + String.format(TRANSFER_URI, accountId))
                .build()
                .toUri();

        log.info("Make transaction from account: " + accountId);
        log.debug("Api request: " + url);

        ResponseEntity<FabrickResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                getHttpEntity(apiKey, transfer),
                FabrickResponse.class
        );

        FabrickResponse body = response.getBody();
        assert body != null : "Response body is null for request: '" + url + "'";
        return body.getPayload();
    }


    private HttpEntity<Object> getHttpEntity(String apiKey, Object body){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Auth-Schema", "S2S");
        headers.set("Api-Key", apiKey);

        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<Object> getHttpEntity(String apiKey){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Auth-Schema", "S2S");
        headers.set("X-Time-Zone", "Europe/Rome");
        headers.set("Api-Key", apiKey);

        return new HttpEntity<>(null, headers);
    }
}
