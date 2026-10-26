package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RagConfigTest {

    @Test
    void oneChunkPerNonBlankLineWithMetadata() {
        List<Document> docs = RagConfig.toDocuments("faq.txt", List.of("Line A", "", "  ", " Line B "));

        assertEquals(2, docs.size());
        assertEquals("Line B", docs.get(1).getText());
        assertEquals("faq.txt", docs.get(1).getMetadata().get("source"));
        assertEquals("faq.txt-1", docs.get(1).getMetadata().get("chunkId"));
    }
}
