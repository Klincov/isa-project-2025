package com.example.demo.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpRateLimitFilter extends OncePerRequestFilter {

    private static final int LIMIT = 5;
    private static final long WINDOW_MS = 60_000;

    private final Map<String, Deque<Long>> attemptsByIp = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("/api/auth/login".equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String ip = getClientIp(req);
        long now = Instant.now().toEpochMilli();

        Deque<Long> q = attemptsByIp.computeIfAbsent(ip, k -> new ArrayDeque<>());

        synchronized (q) {
            while (!q.isEmpty() && (now - q.peekFirst()) > WINDOW_MS) {
                q.pollFirst();
            }
            if (q.size() >= LIMIT) {
                res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                res.getWriter().write("Previse pokusaja prijave. Pokusaj ponovo kasnije.");
                return;
            }
            q.addLast(now);
        }

        chain.doFilter(req, res);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf == null || xf.isBlank()) return request.getRemoteAddr();
        return xf.split(",")[0].trim();
    }
}
