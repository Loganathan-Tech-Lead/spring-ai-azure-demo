package com.bank.assistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
public class BankTools {
    private static final Logger log = LoggerFactory.getLogger(BankTools.class);

    private final AccountClient client;
    private final LoanClient loanClient;

    public BankTools(AccountClient client, LoanClient loanClient) {
        this.client = client;
        this.loanClient = loanClient;
    }

    // The customer id comes from ToolContext, which our code fills in.
    // The model never sees it and cannot change it.
    private String customer(ToolContext ctx) {
        return (String) ctx.getContext().get("customerId");
    }

    @Tool(description = "List the logged-in customer's accounts with id, type, balance and currency")
    public List<Map<String, Object>> listAccounts(ToolContext ctx) {
        return client.accounts(customer(ctx));
    }

    @Tool(description = "Get the current balance of one of the customer's accounts")
    public Map<String, Object> getBalance(
            @ToolParam(description = "Account id such as ACC-1001") String accountId,
            ToolContext ctx) {
        try {
            return client.account(customer(ctx), accountId);
        } catch (HttpClientErrorException.NotFound e) {
            return Map.of("error", "Account not found for this customer.");
        } catch (RestClientException e) {
            log.error("getBalance failed for {}", accountId, e);
            return Map.of("error", "Could not retrieve the balance right now.");
        }
    }

    @Tool(description = "Get the most recent transactions of one of the customer's accounts, newest first")
    public Map<String, Object> getTransactions(
            @ToolParam(description = "Account id such as ACC-1001") String accountId,
            @ToolParam(description = "How many transactions, 1 to 20. Use 5 if not specified",
                       required = false) Integer limit,
            ToolContext ctx) {
        int n = (limit == null) ? 5 : limit;
        try {
            return Map.of("transactions", client.transactions(customer(ctx), accountId, n));
        } catch (HttpClientErrorException.NotFound e) {
            return Map.of("error", "Account not found for this customer.");
        } catch (RestClientException e) {
            log.error("getTransactions failed for {}", accountId, e);
            return Map.of("error", "Could not retrieve transactions right now.");
        }
    }

    @Tool(description = "List the logged-in customer's loans with id, type, outstanding amount and status")
    public Map<String, Object> getLoans(ToolContext ctx) {
        try {
            return Map.of("loans", loanClient.loans(customer(ctx)));
        } catch (RestClientException e) {
            log.error("getLoans failed", e);
            return Map.of("error", "Could not retrieve loans right now.");
        }
    }

    @Tool(description = "Get the details of one of the customer's loans")
    public Map<String, Object> getLoanDetails(
            @ToolParam(description = "Loan id as returned by getLoans") String loanId,
            ToolContext ctx) {
        try {
            return loanClient.loan(customer(ctx), loanId);
        } catch (HttpClientErrorException.NotFound e) {
            return Map.of("error", "Loan not found for this customer.");
        } catch (RestClientException e) {
            log.error("getLoanDetails failed for {}", loanId, e);
            return Map.of("error", "Could not retrieve the loan right now.");
        }
    }
}
