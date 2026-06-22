package com.tarot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarot.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Простой in-memory rate limiter по IP для публичных, неаутентифицированных эндпоинтов
 * (/api/auth/** — брутфорс логина/спам регистраций, /api/leads — спам заявок с лендинга).
 * Для деплоя с несколькими инстансами карту нужно заменить на общий бэкенд (Redis), иначе у каждого
 * инстанса будет свой независимый лимит.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final int maxRequests;
    private final long windowMillis;
    private final List<String> protectedPathPrefixes;

    private final ConcurrentHashMap<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    public RateLimitFilter(ObjectMapper objectMapper,
                            @Value("${ratelimit.auth.max-requests:5}") int maxRequests,
                            @Value("${ratelimit.auth.window-seconds:60}") long windowSeconds,
                            @Value("${ratelimit.protected-paths:/api/auth/,/api/leads}") String protectedPaths) {
        this.objectMapper = objectMapper;
        this.maxRequests = maxRequests;
        this.windowMillis = windowSeconds * 1000;
        this.protectedPathPrefixes = Arrays.stream(protectedPaths.split(",")).map(String::trim).toList();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (protectedPathPrefixes.stream().noneMatch(uri::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String key = clientIp(request);
        long now = Instant.now().toEpochMilli();
        Deque<Long> timestamps = requestLog.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMillis) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= maxRequests) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(
                        new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(),
                                "Слишком много попыток, попробуйте позже")));
                return;
            }
            timestamps.addLast(now);
        }

        chain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
