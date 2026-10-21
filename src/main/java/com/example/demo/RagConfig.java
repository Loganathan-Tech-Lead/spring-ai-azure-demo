package com.example.demo;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ai.reader.TextReader;

import java.util.List;

@Configuration
public class RagConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // 1. Build the vector store using the injected Embedding Model
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        
        // 2. Read and vectorize your knowledge base file
        TextReader textReader = new TextReader(new ClassPathResource("docs/company_policy.txt"));
        List<Document> documents = textReader.get();
        vectorStore.add(documents);
        
        return vectorStore;
    }
}
