package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Records which tools ran during one request. Tool calls run on the request thread, so a ThreadLocal works. */
@Component
public class ToolUsageRecorder {

    private final ThreadLocal<List<String>> tools = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<RagService.Source>> sources = ThreadLocal.withInitial(ArrayList::new);

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
    }
}
