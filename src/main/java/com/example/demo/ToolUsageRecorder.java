package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Per-request state: tools used, sources found, call count and the user's latest message. Tool calls run on the request thread. */
@Component
public class ToolUsageRecorder {

    public static final int MAX_TOOL_CALLS = 6;
    public static final String LIMIT_MESSAGE =
            "Tool call limit reached for this request. Answer with the information you already have.";

    private final ThreadLocal<List<String>> tools = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<RagService.Source>> sources = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<int[]> calls = ThreadLocal.withInitial(() -> new int[1]);
    private final ThreadLocal<String> userMessage = new ThreadLocal<>();
    private final ThreadLocal<String> sessionId = new ThreadLocal<>();

    public void setContext(String session, String message) {
        sessionId.set(session);
        userMessage.set(message);
    }

    public String userMessage() {
        return userMessage.get();
    }

    /** Counts a tool call. Returns false once the per-request cap is reached. */
    public boolean tryCall(String name) {
        if (calls.get()[0] >= MAX_TOOL_CALLS) {
            return false;
        }
        calls.get()[0]++;
        recordTool(name);
        return true;
    }

    public void recordTool(String name) {
        if (!tools.get().contains(name)) {
            tools.get().add(name);
        }
    }

    public void recordSources(List<RagService.Source> found) {
        sources.get().addAll(found);
    }

    public List<String> tools() {
        return List.copyOf(tools.get());
    }

    public List<RagService.Source> sources() {
        return List.copyOf(sources.get());
    }

    public void clear() {
        tools.remove();
        sources.remove();
        calls.remove();
        userMessage.remove();
        sessionId.remove();
    }
}
