package com.example.demo;

import java.util.List;
import java.util.regex.Pattern;

/** Redacts anything that looks like a credential before a response leaves the app. */
public final class SecretFilter {

    private static final Pattern WIFI_QUOTED = Pattern.compile(
            "(?i)(wi-?fi\\s+(?:password|passcode)[^\"'\\n]{0,60}[\"'])[^\"']+([\"'])");
    private static final Pattern WIFI_PLAIN = Pattern.compile(
            "(?i)(wi-?fi\\s+(?:password|passcode)\\s*(?:is|:|=)\\s*)\\S+");
    private static final Pattern TOKEN = Pattern.compile(
            "\\b(?:ghp|gho|github_pat|sk)[_-][A-Za-z0-9_\\-]{20,}");
    private static final Pattern GENERIC = Pattern.compile(
            "(?i)\\b(password|passwd|pwd|api[-_ ]?key|secret)\\b\\s*(?:is|[:=])\\s*[\"']?[^\\s\"']+[\"']?");
    private static final Pattern LONG_KEY = Pattern.compile("\\b[A-Za-z0-9]{32,}\\b");

    private SecretFilter() {}

    public static String redact(String text) {
        if (text == null) {
            return "";
        }
        String out = WIFI_QUOTED.matcher(text).replaceAll("$1[REDACTED]$2");
        out = WIFI_PLAIN.matcher(out).replaceAll("$1[REDACTED]");
        out = TOKEN.matcher(out).replaceAll("[REDACTED_TOKEN]");
        out = GENERIC.matcher(out).replaceAll("$1: [REDACTED]");
        out = LONG_KEY.matcher(out).replaceAll("[REDACTED_KEY]");
        return out;
    }

    public static List<RagService.Source> redactSources(List<RagService.Source> sources) {
        return sources.stream()
                .map(s -> new RagService.Source(s.file(), redact(s.snippet())))
                .toList();
    }
}
