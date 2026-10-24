package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AgentV2Controller {

    public record AgentRequest(String message) {}

    private final AgentService agentService;

    public AgentV2Controller(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/api/v2/agent/chat")
    public AgentService.AgentResponse chat(@RequestBody AgentRequest request,
                                           @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        String message = request == null ? null : request.message();
        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message must not be empty");
        }
        if (message.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message must be at most 2000 characters");
        }
        return agentService.chat(message, sessionId);
    }
}
