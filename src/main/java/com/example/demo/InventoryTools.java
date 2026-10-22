package com.example.demo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryTools {

    private final RestClient restClient;

    public InventoryTools() {
<<<<<<< HEAD
=======
        // Pointing to our own app to simulate a remote microservice on port 9090
>>>>>>> c85c00652c4e031559fe39ddf7b8f5f7de5540df
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:9090")
                .build();
    }

    @Tool(description = "Check the real-time stock availability of an item in the e-commerce inventory")
    public String checkInventory(@ToolParam(description = "The name of the product, e.g., laptop, phone") String item) {
        try {
<<<<<<< HEAD
=======
            // The "Distributed Transaction" - calling the remote microservice
>>>>>>> c85c00652c4e031559fe39ddf7b8f5f7de5540df
            return restClient.get()
                    .uri("/api/inventory/check?item={item}", item)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
<<<<<<< HEAD
=======
            // RESILIENCE: If the network fails, the microservice is down, or it times out
>>>>>>> c85c00652c4e031559fe39ddf7b8f5f7de5540df
            System.err.println("Inventory service failed: " + e.getMessage());
            return "Error: The inventory system is currently unreachable. Please try again later.";
        }
    }
}
