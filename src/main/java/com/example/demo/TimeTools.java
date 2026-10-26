package com.example.demo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeTools {

    private final ToolUsageRecorder recorder;

    public TimeTools(ToolUsageRecorder recorder) {
        this.recorder = recorder;
    }

    @Tool(description = "Get the current date and time")
    public String getCurrentDateTime() {
        if (!recorder.tryCall("getCurrentDateTime")) { return ToolUsageRecorder.LIMIT_MESSAGE; }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "The current time is: " + now.format(formatter);
    }
}
