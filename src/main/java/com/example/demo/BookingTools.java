package com.example.demo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingTools {

    private final BookingService bookingService;
    private final ToolUsageRecorder recorder;

    public BookingTools(BookingService bookingService, ToolUsageRecorder recorder) {
        this.bookingService = bookingService;
        this.recorder = recorder;
    }

    @Tool(description = "List free onboarding slots for a date. The date must be in YYYY-MM-DD format.")
    public String listAvailableSlots(String isoDate) {
        recorder.recordTool("listAvailableSlots");
        try {
            List<String> free = bookingService.availableSlots(isoDate);
            return free.isEmpty() ? "No free slots on " + isoDate : "Free slots on " + isoDate + ": " + free;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @Tool(description = "Book an onboarding slot. First propose the booking to the user (name, date, slot) and ask them to confirm. "
            + "Set confirmed=true ONLY after the user has clearly said yes in this conversation. Otherwise set confirmed=false.")
    public String bookOnboardingSlot(String employeeName, String isoDate, String slot, boolean confirmed) {
        recorder.recordTool("bookOnboardingSlot");
        if (!confirmed) {
            return "NOT BOOKED. Ask the user to confirm: " + employeeName + ", " + isoDate + " at " + slot + ".";
        }
        try {
            return bookingService.book(employeeName, isoDate, slot)
                    ? "Booked " + employeeName + " on " + isoDate + " at " + slot + "."
                    : "That slot is not available. Offer another slot.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
