package com.tarot.dto.auth;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs
) {
    public AuthResponse(String token, long expiresInMs) {
        this(token, "Bearer", expiresInMs);
    }
}
