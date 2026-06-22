package com.tarot.dto.lead;

import com.tarot.entity.Lead;

import java.time.LocalDateTime;

public record LeadResponse(
        Long id,
        String name,
        String contact,
        Long serviceId,
        String serviceName,
        String question,
        Lead.LeadStatus status,
        LocalDateTime createdAt
) {
}
