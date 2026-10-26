package com.example.demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Fixed-window limiter: N requests per minute per client IP, for /api/v2/** only. In memory, resets on restart. */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MS = 60_000;

    private final int limit;
    private final Map<String, long[]> windows = new ConcurrentHashMap<>(); // ip -> {windowStart, count}

    public RateLimitFilter(@Value("${app.rate-limit.per-minute:20}") int limit) {
        this.limit = limit;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/v2/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!allow(clientIp(request), System.currentTimeMillis())) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"error\":\"too many requests, try again in a minute\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    boolean allow(String ip, long nowMs) {
        if (windows.size() > 1000) {
            windows.entrySet().removeIf(e -> nowMs - e.getValue()[0] >= WINDOW_MS);
        }
        long[] w = windows.compute(ip, (k, cur) ->
                (cur == null || nowMs - cur[0] >= WINDOW_MS) ? new long[]{nowMs, 1} : new long[]{cur[0], cur[1] + 1});
        return w[1] <= limit;
    }

    /** Behind Azure App Service the real client is in X-Forwarded-For (may carry a :port). A client can spoof this header. */
    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            String ip = forwarded.split(",")[0].trim();
            if (ip.indexOf(':') == ip.lastIndexOf(':') && ip.contains(":")) {
                ip = ip.substring(0, ip.indexOf(':')); // IPv4:port
            }
            return ip;
        }
        return request.getRemoteAddr();
    }
}
