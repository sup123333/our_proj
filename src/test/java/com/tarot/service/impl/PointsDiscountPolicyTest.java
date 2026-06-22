package com.tarot.service.impl;

import com.tarot.config.LoyaltyProperties;
import com.tarot.entity.Client;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PointsDiscountPolicyTest {

    private final LoyaltyProperties loyaltyProperties = new LoyaltyProperties();
    private final PointsDiscountPolicy policy = new PointsDiscountPolicy(loyaltyProperties);

    @Test
    void noDiscount_whenBelowThreshold() {
        Client client = Client.builder().totalPoints(loyaltyProperties.getPointsForDiscount() - 1).build();

        BigDecimal discount = policy.calculateDiscount(client, BigDecimal.valueOf(1000));

        assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void appliesDiscount_whenAtThreshold() {
        Client client = Client.builder().totalPoints(loyaltyProperties.getPointsForDiscount()).build();

        BigDecimal discount = policy.calculateDiscount(client, BigDecimal.valueOf(1000));

        BigDecimal expected = BigDecimal.valueOf(1000)
                .multiply(BigDecimal.valueOf(loyaltyProperties.getDiscountPercent()))
                .divide(BigDecimal.valueOf(100));
        assertThat(discount).isEqualByComparingTo(expected);
    }
}
