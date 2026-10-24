package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RagServiceTest {

    @Test
    void mapsDocumentsToSources() {
        VectorStore store = mock(VectorStore.class);
        Document doc = new Document("Sick Leave: 10 days", Map.<String, Object>of("source", "leave_policy.txt"));
        when(store.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

        RagService service = new RagService(store, 0.3, 4);
        List<Document> found = service.retrieve("sick days?");
        List<RagService.Source> sources = service.toSources(found);

        assertEquals(1, sources.size());
        assertEquals("leave_policy.txt", sources.get(0).file());
        assertEquals("Sick Leave: 10 days", sources.get(0).snippet());
        assertEquals("Sick Leave: 10 days", service.buildContext(found));
    }

    @Test
    void emptyResultIsHandled() {
        VectorStore store = mock(VectorStore.class);
        when(store.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

        RagService service = new RagService(store, 0.3, 4);

        assertTrue(service.retrieve("anything").isEmpty());
    }
}
