package com.example.demo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeTools {

    @Tool(description = "Get the current date and time")
    public String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "The current time is: " + now.format(formatter);
    }
}
