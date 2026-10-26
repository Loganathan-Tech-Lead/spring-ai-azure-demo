package com.example.demo;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    public record Source(String file, String snippet) {}

    private final VectorStore vectorStore;
    private final double similarityThreshold;
    private final int defaultTopK;

    public RagService(VectorStore vectorStore,
                      @Value("${app.rag.similarity-threshold:0.3}") double similarityThreshold,
                      @Value("${app.rag.top-k:4}") int defaultTopK) {
        this.vectorStore = vectorStore;
        this.similarityThreshold = similarityThreshold;
        this.defaultTopK = defaultTopK;
    }

    public List<Document> retrieve(String question) {
        return retrieve(question, defaultTopK);
    }

    /** Returns the closest chunks, dropping anything below the similarity threshold. */
    public List<Document> retrieve(String question, int topK) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(topK)
                        .similarityThreshold(similarityThreshold)
                        .build());
        return results == null ? List.of() : results;
    }

    public String buildContext(List<Document> docs) {
        return docs.stream().map(Document::getText).collect(Collectors.joining("\n"));
    }

    public List<Source> toSources(List<Document> docs) {
        return docs.stream()
                .map(d -> new Source(
                        String.valueOf(d.getMetadata().getOrDefault("source", "unknown")),
                        d.getText()))
                .toList();
    }
}
