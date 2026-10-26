package com.bank.assistant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class AccountClient {
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<Map<String, Object>> MAP =
            new ParameterizedTypeReference<>() {};

    private final RestClient http;

    public AccountClient(@Value("${account-service.url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    public List<Map<String, Object>> accounts(String customerId) {
        return http.get().uri("/api/accounts")
                .header("X-Customer-Id", customerId)
                .retrieve().body(LIST);
    }

    public Map<String, Object> account(String customerId, String accountId) {
        return http.get().uri("/api/accounts/{id}", accountId)
                .header("X-Customer-Id", customerId)
                .retrieve().body(MAP);
    }

        public List<Map<String, Object>> transactions(String customerId, String accountId, int limit) {
        return http.get()
                .uri(u -> u.path("/api/accounts/{id}/transactions")
                        .queryParam("limit", limit)
                        .build(accountId))
                .header("X-Customer-Id", customerId)
                .retrieve().body(LIST);
    }
}