package com.tarot.dto.service;

import java.math.BigDecimal;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer pointsReward,
        Boolean active
) {
}
