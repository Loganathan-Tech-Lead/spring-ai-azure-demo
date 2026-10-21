package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final TimeTools timeTools;

    // Inject the VectorStore we created in RagConfig
    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory, TimeTools timeTools, VectorStore vectorStore) {
        this.timeTools = timeTools;
        
        this.chatClient = builder
                .defaultSystem("You are a helpful AI assistant for Loganathan Tech Solutions. Answer questions using the provided context.")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // Add the RAG Advisor here!
                        new QuestionAnswerAdvisor(vectorStore) 
                )
                .build();
    }

    @GetMapping("/api/chat")
    public String chat(@RequestParam(value = "message") String message) {
        return chatClient.prompt()
                .user(message)
                .tools(timeTools)
                .call()
                .content();
    }
}
