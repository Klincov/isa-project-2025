package com.example.demo.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class VisitorActivityFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "vid";
    private static final int COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24 * 30; // 30 dana

    private final MeterRegistry meterRegistry;

    public VisitorActivityFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String vid = readCookie(request, COOKIE_NAME);

        boolean created = false;
        if (vid == null || vid.isBlank()) {
            created = true;
            vid = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(COOKIE_NAME, vid);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
            response.addCookie(cookie);
        }

        System.out.println("VisitorActivityFilter HIT: " + request.getMethod() + " " + request.getRequestURI()
                + " created=" + created + " vid=" + vid);


        Counter.builder("app_user_activity_total")
                .description("Count of requests per visitor (anon + logged)")
                .tag("vid", vid)
                .register(meterRegistry)
                .increment();

        filterChain.doFilter(request, response);
    }

    private static String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
