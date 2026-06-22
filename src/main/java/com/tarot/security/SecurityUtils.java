package com.tarot.security;

import org.springframework.security.core.context.SecurityContextHolder;

// Единая точка получения email текущего аутентифицированного клиента из SecurityContext.
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String currentEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Нет аутентифицированного пользователя");
        }
        return (String) authentication.getPrincipal();
    }
}
