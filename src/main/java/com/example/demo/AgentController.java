package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final ChatClient chatClient;

    public AgentController(ChatClient.Builder builder, WeatherService weatherService) {
        this.chatClient = builder
                .defaultTools(weatherService)
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}