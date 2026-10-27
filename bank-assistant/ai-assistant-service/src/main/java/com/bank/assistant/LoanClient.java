package com.bank.assistant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class LoanClient {
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<Map<String, Object>> MAP =
            new ParameterizedTypeReference<>() {};

    private final RestClient http;

    public LoanClient(@Value("${loan-service.url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    public List<Map<String, Object>> loans(String customerId) {
        return http.get().uri("/api/loans")
                .header("X-Customer-Id", customerId)
                .retrieve().body(LIST);
    }

    public Map<String, Object> loan(String customerId, String loanId) {
        return http.get().uri("/api/loans/{id}", loanId)
                .header("X-Customer-Id", customerId)
                .retrieve().body(MAP);
    }
}
