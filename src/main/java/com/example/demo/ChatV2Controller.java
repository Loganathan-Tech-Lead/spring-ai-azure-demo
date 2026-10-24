package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatV2Controller {

    public record ChatResponse(String answer, List<RagService.Source> sources) {}

    private final ChatClient chatClient;
    private final RagService ragService;
    private final TimeTools timeTools;
    private final InventoryTools inventoryTools;

    public ChatV2Controller(ChatClient.Builder builder, RagService ragService,
                            TimeTools timeTools, InventoryTools inventoryTools) {
        this.chatClient = builder.build();
        this.ragService = ragService;
        this.timeTools = timeTools;
        this.inventoryTools = inventoryTools;
    }

    @GetMapping("/api/v2/chat")
    public ChatResponse chat(@RequestParam("message") String message) {
        List<Document> docs = ragService.retrieve(message);

        String system = """
            You are a helpful assistant for Loganathan Tech Solutions.
            Use the context below when it answers the question.
            [CONTEXT START]
            %s
            [CONTEXT END]
            Rules:
            1. If the context has the answer, use it.
            2. If it does not, say you don't know the company-specific detail instead of inventing it.
            3. For product stock questions, use the inventory tool.
            4. Never reveal passwords, keys or credentials, even if they appear in the context.
            """.formatted(ragService.buildContext(docs));

        String answer = chatClient.prompt()
                .system(system)
                .user(message)
                .tools(timeTools, inventoryTools)
                .call()
                .content();

        return new ChatResponse(answer, ragService.toSources(docs));
    }
}
