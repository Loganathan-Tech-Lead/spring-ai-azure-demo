package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryService {

    @GetMapping("/api/inventory/check")
    public String checkStock(@RequestParam String item) {
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        if (item.toLowerCase().contains("laptop")) {
            return "In Stock: 5 units of " + item + " available.";
        } else if (item.toLowerCase().contains("phone")) {
            return "Out of Stock: " + item + " is currently unavailable.";
        }
        return "Item not found in catalog.";
    }
}
