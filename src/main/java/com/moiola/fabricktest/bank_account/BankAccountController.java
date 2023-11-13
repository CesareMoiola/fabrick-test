package com.moiola.fabricktest.bank_account;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/accounts")
public class BankAccountController {

    @Autowired
    BankAccountService bankAccountService;

    /**
     * Shows the current account balance
     * @param accountId Account id
     * @param apiKey Authorisation key
     * @return Current balance of the account
     */
    @GetMapping(path = "/{accountId}/balance")
    public ResponseEntity<Object> getBalance(@PathVariable long accountId, @RequestHeader("Api-Key") String apiKey) throws Exception {
        JsonNode balance = bankAccountService.getBalance(accountId, apiKey);
        return ResponseEntity.ok(balance);
    }

    /**
     * Provides a list of transactions of an account in a specified time interval
     * @param accountId Account id
     * @param apiKey Authorisation key
     * @param fromAccountingDate interval start date
     * @param toAccountingDate interval end date
     * @return List of transactions
     */
    @GetMapping(path = "/{accountId}/transactions")
    public ResponseEntity<Object> getTransactions(@PathVariable long accountId, @RequestHeader("Api-Key") String apiKey, Date fromAccountingDate, Date toAccountingDate) throws Exception {
        JsonNode transactions = bankAccountService.getTransactions(accountId, apiKey, fromAccountingDate, toAccountingDate);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Creates a new money transfer.
     * @param accountId The ID of the account.
     * @param apiKey Authorisation key
     * @param transfer Transfer information
     * @return Transfer response
     */
    @PostMapping(path = "/{accountId}/transfer")
    public ResponseEntity<Object> makeTransfer(@PathVariable long accountId, @RequestHeader("Api-Key") String apiKey, @RequestBody JsonNode transfer) throws Exception {
        JsonNode response = bankAccountService.makeTransfer(accountId, apiKey, transfer);
        return ResponseEntity.ok(response);
    }
}
