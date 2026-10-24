package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class EscalationTool {

    private static final Logger log = LoggerFactory.getLogger(EscalationTool.class);
    private final AtomicInteger counter = new AtomicInteger(1000);
    private final ToolUsageRecorder recorder;

    public EscalationTool(ToolUsageRecorder recorder) {
        this.recorder = recorder;
    }

    @Tool(description = "Hand the request to a human when you cannot answer, the user is upset, "
            + "or the request is outside your allowed actions. Give a short reason.")
    public String escalateToHuman(String reason) {
        recorder.recordTool("escalateToHuman");
        String ticket = "TKT-" + counter.incrementAndGet();
        log.info("Escalation {} created", ticket); // reason intentionally not logged
        return "Escalated. Ticket " + ticket + ". A human will follow up.";
    }
}
