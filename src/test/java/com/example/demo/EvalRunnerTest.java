package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Calls a RUNNING app. Disabled unless -Deval=true. Never runs during a normal build. */
@EnabledIfSystemProperty(named = "eval", matches = "true")
class EvalRunnerTest {

    @Test
    void runEvaluation() throws Exception {
        String base = System.getProperty("eval.url", "http://localhost:8081");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode questions;
        try (InputStream in = getClass().getResourceAsStream("/eval/questions.json")) {
            questions = mapper.readTree(in);
        }
        HttpClient client = HttpClient.newHttpClient();
        int pass = 0;
        int n = 0;
        System.out.printf("%n%-3s %-5s %s%n", "#", "RESULT", "QUESTION");
        for (JsonNode q : questions) {
            n++;
            String question = q.get("q").asText();
            int wantStatus = q.path("status").asInt(200);
            String expect = q.path("expect").asText("").toLowerCase();
            String forbid = q.path("forbid").asText("").toLowerCase();

            HttpRequest req = HttpRequest.newBuilder(URI.create(base + "/api/v2/agent/chat"))
                    .header("Content-Type", "application/json")
                    .header("X-Session-Id", "eval-" + n)
                    .timeout(Duration.ofSeconds(90))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(Map.of("message", question))))
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            String answer = res.statusCode() == 200
                    ? mapper.readTree(res.body()).path("answer").asText().toLowerCase() : "";

            boolean ok = res.statusCode() == wantStatus
                    && (expect.isEmpty() || answer.contains(expect))
                    && (forbid.isEmpty() || !answer.contains(forbid));
            if (ok) {
                pass++;
            }
            System.out.printf("%-3d %-6s %s%n", n, ok ? "PASS" : "FAIL", question);
            Thread.sleep(3200); // stay under the 20 requests/minute limit
        }
        System.out.printf("%nScore: %d / %d%n", pass, n);
        assertEquals(n, pass, "some evaluation questions failed");
    }
}
