package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class RagConfig {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);
    private static final String[] PATTERNS = {"classpath:docs/*.txt", "classpath:docs/*.md"};

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        long start = System.currentTimeMillis();
        int chunks = 0;
        int files = 0;

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (String pattern : PATTERNS) {
            Resource[] resources;
            try {
                resources = resolver.getResources(pattern);
            } catch (IOException e) {
                log.warn("Could not scan {}: {}", pattern, e.getMessage());
                continue;
            }
            for (Resource resource : resources) {
                String name = resource.getFilename();
                try {
                    List<Document> docs = toDocuments(name, readLines(resource));
                    if (docs.isEmpty()) {
                        continue;
                    }
                    store.add(docs); // one embedding call per chunk
                    chunks += docs.size();
                    files++;
                } catch (Exception e) {
                    // Do not crash startup if Azure OpenAI is unavailable.
                    log.warn("Skipping {} because embedding failed: {}", name, e.getMessage());
                }
            }
        }
        log.info("RAG ready: {} chunks from {} files in {} ms",
                chunks, files, System.currentTimeMillis() - start);
        return store;
    }

    /** One chunk per non-blank line, with source and chunkId metadata. Static so it is easy to unit test. */
    static List<Document> toDocuments(String fileName, List<String> lines) {
        List<Document> docs = new ArrayList<>();
        int index = 0;
        for (String line : lines) {
            String text = line.trim();
            if (text.isEmpty()) {
                continue;
            }
            String chunkId = fileName + "-" + index++;
            docs.add(new Document(text, Map.<String, Object>of("source", fileName, "chunkId", chunkId)));
        }
        return docs;
    }

    // Read through an InputStream: the files live inside the JAR on Azure.
    private static List<String> readLines(Resource resource) throws IOException {
        try (var in = resource.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().toList();
        }
    }
}
