package com.example.demo;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/** Basic input checks. The injection patterns are a first line of defence only; the system prompt is the second. */
public final class InputGuard {

    static final int MAX_LENGTH = 2000;

    private static final List<Pattern> INJECTION = List.of(
            Pattern.compile("ignore\\s+(all\\s+|any\\s+)?(the\\s+)?(previous|prior|above|earlier)\\s+(instructions|rules|prompts?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("disregard\\s+.{0,30}(instructions|rules)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(reveal|show|print|repeat|display|tell me).{0,30}(system prompt|your instructions|hidden prompt)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("you\\s+are\\s+now\\s+(dan|in\\s+developer\\s+mode)", Pattern.CASE_INSENSITIVE));

    private InputGuard() {}

    /** Returns an error message if the input must be rejected, otherwise empty. */
    public static Optional<String> validate(String message) {
        if (message == null || message.isBlank()) {
            return Optional.of("message must not be empty");
        }
        if (message.length() > MAX_LENGTH) {
            return Optional.of("message must be at most " + MAX_LENGTH + " characters");
        }
        for (Pattern p : INJECTION) {
            if (p.matcher(message).find()) {
                return Optional.of("this request cannot be processed");
            }
        }
        return Optional.empty();
    }
}
