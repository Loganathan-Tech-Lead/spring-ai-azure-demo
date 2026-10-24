package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingToolsTest {

    private final ToolUsageRecorder recorder = new ToolUsageRecorder();
    private final BookingTools tools = new BookingTools(new BookingService(), recorder);

    @Test
    void notConfirmedDoesNotBook() {
        String result = tools.bookOnboardingSlot("Asha", "2026-11-02", "10:00", false);
        assertTrue(result.startsWith("NOT BOOKED"));
        assertTrue(tools.listAvailableSlots("2026-11-02").contains("10:00"));
    }

    @Test
    void confirmedBookingTakesTheSlot() {
        assertTrue(tools.bookOnboardingSlot("Asha", "2026-11-03", "10:00", true).startsWith("Booked"));
        assertFalse(tools.listAvailableSlots("2026-11-03").contains("10:00"));
        assertTrue(tools.bookOnboardingSlot("Ravi", "2026-11-03", "10:00", true).contains("not available"));
    }

    @Test
    void badDateGivesMessageNotException() {
        assertTrue(tools.listAvailableSlots("tomorrow").contains("YYYY-MM-DD"));
    }

    @Test
    void recorderTracksToolsOncePerRequest() {
        tools.listAvailableSlots("2026-11-04");
        tools.listAvailableSlots("2026-11-05");
        assertEquals(1, recorder.tools().size());
        recorder.clear();
        assertTrue(recorder.tools().isEmpty());
    }
}
