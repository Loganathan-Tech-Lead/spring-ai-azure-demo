package com.example.demo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class BookingTools {

    private static final Pattern CONFIRM = Pattern.compile(
            "(?i)\\b(yes|yep|yeah|sure|confirm(ed)?|go ahead|proceed|please do|ok(ay)?)\\b");

    private final BookingService bookingService;
    private final ToolUsageRecorder recorder;

    public BookingTools(BookingService bookingService, ToolUsageRecorder recorder) {
        this.bookingService = bookingService;
        this.recorder = recorder;
    }

    @Tool(description = "List free onboarding slots for a date. The date must be in YYYY-MM-DD format.")
    public String listAvailableSlots(String isoDate) {
        if (!recorder.tryCall("listAvailableSlots")) { return ToolUsageRecorder.LIMIT_MESSAGE; }
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
        if (!recorder.tryCall("bookOnboardingSlot")) { return ToolUsageRecorder.LIMIT_MESSAGE; }
        String details = employeeName + ", " + isoDate + " at " + slot;
        if (!confirmed) {
            return "NOT BOOKED. Ask the user to confirm: " + details + ".";
        }
        // Server-side check: the user's latest message must itself contain a confirmation.
        String latest = recorder.userMessage();
        if (latest == null || !CONFIRM.matcher(latest).find()) {
            return "NOT BOOKED. The user has not confirmed in their latest message. Ask them to confirm: " + details + ".";
        }
        try {
            return bookingService.book(employeeName, isoDate, slot)
                    ? "Booked " + details + "."
                    : "That slot is not available. Offer another slot.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
