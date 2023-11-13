package com.moiola.fabricktest.bank_account;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Date;

public interface BankAccountService {

    JsonNode getBalance(long accountId, String apiKey ) throws Exception;

    JsonNode getTransactions(long accountId, String apiKey, Date fromAccountingDate, Date toAccountingDate) throws Exception;

    JsonNode makeTransfer(long accountId, String apiKey, JsonNode transfer) throws Exception;
}
