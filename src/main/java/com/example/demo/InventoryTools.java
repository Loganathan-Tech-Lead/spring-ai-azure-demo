package com.example.demo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryTools {

    private final RestClient restClient;

    public InventoryTools() {
        // Pointing to our own app to simulate a remote microservice on port 9090
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:9090")
                .build();
    }

    @Tool(description = "Check the real-time stock availability of an item in the e-commerce inventory")
    public String checkInventory(@ToolParam(description = "The name of the product, e.g., laptop, phone") String item) {
        try {
            // The "Distributed Transaction" - calling the remote microservice
            return restClient.get()
                    .uri("/api/inventory/check?item={item}", item)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            // RESILIENCE: If the network fails, the microservice is down, or it times out
            System.err.println("Inventory service failed: " + e.getMessage());
            return "Error: The inventory system is currently unreachable. Please try again later.";
        }
    }
}
