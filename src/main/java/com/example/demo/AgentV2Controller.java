package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class AgentV2Controller {

    public record AgentRequest(String message) {}

    private final AgentService agentService;

    public AgentV2Controller(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/api/v2/agent/chat")
    public ResponseEntity<Object> chat(@RequestBody AgentRequest request,
                                       @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        Optional<String> problem = InputGuard.validate(request == null ? null : request.message());
        if (problem.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", problem.get()));
        }
        return ResponseEntity.ok(agentService.chat(request.message(), sessionId));
    }
}
