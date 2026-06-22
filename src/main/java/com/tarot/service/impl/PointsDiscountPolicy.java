package com.tarot.service.impl;

import com.tarot.config.LoyaltyProperties;
import com.tarot.entity.Client;
import com.tarot.service.DiscountPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PointsDiscountPolicy implements DiscountPolicy {

    private final LoyaltyProperties loyaltyProperties;

    @Override
    public BigDecimal calculateDiscount(Client client, BigDecimal price) {
        if (client.getTotalPoints() < loyaltyProperties.getPointsForDiscount()) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(loyaltyProperties.getDiscountPercent()))
                .divide(BigDecimal.valueOf(100));
    }
}
