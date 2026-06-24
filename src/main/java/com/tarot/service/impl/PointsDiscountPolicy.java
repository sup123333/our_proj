package com.tarot.service.impl;

import com.tarot.config.LoyaltyProperties;
import com.tarot.entity.Client;
import com.tarot.service.DiscountPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Скидка за накопленные баллы лояльности. На своих вопросах (без объёмной скидки)
// баллы дают меньший процент, чем на темах из подобранных карточек.
@Component
@RequiredArgsConstructor
public class PointsDiscountPolicy implements DiscountPolicy {

    private final LoyaltyProperties loyaltyProperties;

    @Override
    public BigDecimal calculateDiscount(Client client, BigDecimal price, boolean ownQuestion) {
        if (client.getTotalPoints() < loyaltyProperties.getPointsForDiscount()) {
            return BigDecimal.ZERO;
        }
        int percent = ownQuestion
                ? loyaltyProperties.getOwnQuestionDiscountPercent()
                : loyaltyProperties.getDiscountPercent();
        return price.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100));
    }
}
