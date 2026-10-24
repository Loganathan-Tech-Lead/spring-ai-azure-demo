package com.example.demo;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicySearchTool {

    private final RagService ragService;
    private final ToolUsageRecorder recorder;

    public PolicySearchTool(RagService ragService, ToolUsageRecorder recorder) {
        this.ragService = ragService;
        this.recorder = recorder;
    }

    @Tool(description = "Search the company handbook (leave, IT, working hours, expenses, onboarding, notice period). "
            + "Use this for any question about company policies or rules.")
    public String searchPolicies(String question) {
        recorder.recordTool("searchPolicies");
        List<Document> docs = ragService.retrieve(question);
        recorder.recordSources(ragService.toSources(docs));
        return docs.isEmpty() ? "No matching policy found." : ragService.buildContext(docs);
    }
}
