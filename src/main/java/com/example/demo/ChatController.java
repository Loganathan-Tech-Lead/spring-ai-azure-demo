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

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory, TimeTools timeTools, VectorStore vectorStore) {
        this.timeTools = timeTools;

        // NOTICE: We removed {query} from the template!
        String customRagPrompt = """
            You are a helpful AI assistant for Loganathan Tech Solutions.
            Answer the user's question using the provided context if it contains the answer.
            If the context does not contain the answer, use your general knowledge to answer.
            
            Context: {question_answer_context}
            """;

        this.chatClient = builder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // Pass the string directly to the constructor
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build(), customRagPrompt)
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
