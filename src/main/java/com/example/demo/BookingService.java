package com.example.demo;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookingService {

    static final List<String> SLOTS = List.of("10:00", "14:00", "16:00");

    // key: date + " " + slot, value: employee name
    private final Map<String, String> bookings = new ConcurrentHashMap<>();

    public List<String> availableSlots(String isoDate) {
        LocalDate date = parse(isoDate);
        return SLOTS.stream().filter(s -> !bookings.containsKey(date + " " + s)).toList();
    }

    /** Returns true if the slot was free and is now booked. */
    public boolean book(String employeeName, String isoDate, String slot) {
        LocalDate date = parse(isoDate);
        if (!SLOTS.contains(slot)) {
            return false;
        }
        return bookings.putIfAbsent(date + " " + slot, employeeName) == null;
    }

    private LocalDate parse(String isoDate) {
        try {
            return LocalDate.parse(isoDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format");
        }
    }
}
