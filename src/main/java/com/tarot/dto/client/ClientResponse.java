package com.tarot.dto.client;

import com.tarot.entity.Client;

import java.time.LocalDateTime;

// Пароль сюда никогда не попадает — это сознательная граница между entity и тем, что уходит клиенту.
public record ClientResponse(
        Long id,
        String name,
        String contact,
        String phone,
        String telegram,
        Client.ClientStatus status,
        Integer totalPoints,
        Long sessionsCount,
        LocalDateTime createdAt,
        LocalDateTime lastSessionAt
) {
}
