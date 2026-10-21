package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final TimeTools timeTools;

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory, TimeTools timeTools) {
        this.timeTools = timeTools;
        
        // Build the ChatClient with Memory and a System Prompt
        this.chatClient = builder
                .defaultSystem("You are a helpful and concise AI assistant. If asked for the time, use your tools.")
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    @GetMapping("/api/chat")
    public String chat(@RequestParam(value = "message") String message) {
        return chatClient.prompt()
                .user(message)
                .tools(timeTools) // Enable the Time Tool
                .call()
                .content();
    }
}
