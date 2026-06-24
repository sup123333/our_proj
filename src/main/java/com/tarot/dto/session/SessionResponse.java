package com.tarot.dto.session;

import com.tarot.entity.Session;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SessionResponse(
        Long id,
        Long clientId,
        String clientName,
        Long serviceId,
        String serviceName,
        BigDecimal price,
        Boolean ownQuestion,
        BigDecimal discountApplied,
        BigDecimal finalPrice,
        Session.SessionStatus status,
        String clientQuestion,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        LocalDateTime completedAt,
        Integer pointsEarned
) {
}
