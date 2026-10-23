package com.example.demo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @Tool(description = "Get the current weather for a location")
    public String getCurrentWeather(String location) {
        return "The weather in " + location + " is 72°F and sunny";
    }
}