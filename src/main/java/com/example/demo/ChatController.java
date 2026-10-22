package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final TimeTools timeTools;
    private final InventoryTools inventoryTools;
    private final VectorStore vectorStore;

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory,
                          TimeTools timeTools, InventoryTools inventoryTools, VectorStore vectorStore) {
        this.timeTools = timeTools;
        this.inventoryTools = inventoryTools;
        this.vectorStore = vectorStore;

        // We only keep Chat Memory here. No more QuestionAnswerAdvisor!
        this.chatClient = builder
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    @GetMapping("/api/chat")
    public String chat(@RequestParam(value = "message") String message) {
        // 1. Manually search the Vector Database for relevant documents
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder().query(message).topK(3).build()
        );
        
        // 2. Combine the found text into a single string
        String context = similarDocuments != null ? 
                similarDocuments.stream().map(Document::getText).collect(Collectors.joining("\n")) : "";

        // 3. Build a smart system prompt
        String systemPrompt = """
            You are a helpful AI assistant for Loganathan Tech Solutions.
            Here is some internal context you can use to answer the user's question:
            [CONTEXT START]
            %s
            [CONTEXT END]
            
            Rules:
            1. If the context contains the answer, use it.
            2. If the context does not contain the answer, use your general knowledge.
            3. If the user asks about product stock (e.g., laptops, phones), you MUST use your inventory tool.
            """.formatted(context);

        // 4. Call the LLM with the custom prompt and tools
        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .tools(timeTools, inventoryTools)
                .call()
                .content();
    }
}
