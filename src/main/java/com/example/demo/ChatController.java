package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final TimeTools timeTools;
    private final InventoryTools inventoryTools; // New Tool

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory, 
                          TimeTools timeTools, InventoryTools inventoryTools, VectorStore vectorStore) {
        this.timeTools = timeTools;
        this.inventoryTools = inventoryTools;

        String customRagPrompt = """
            You are a helpful AI assistant for Loganathan Tech Solutions.
            Answer the user's question using the provided context if it contains the answer.
            If the context does not contain the answer, use your general knowledge to answer.
            If the user asks about product stock, use your inventory tool.
            
            Context: {question_answer_context}
            """;

        this.chatClient = builder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build(), customRagPrompt)
                )
                .build();
    }

    @GetMapping("/api/chat")
    public String chat(@RequestParam(value = "message") String message) {
        return chatClient.prompt()
                .user(message)
                // Register BOTH tools here
                .tools(timeTools, inventoryTools) 
                .call()
                .content();
    }
}
