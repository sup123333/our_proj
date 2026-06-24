package com.tarot.service.impl;

import com.tarot.config.LoyaltyProperties;
import com.tarot.entity.Client;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VolumeDiscountPolicyTest {

    private final LoyaltyProperties loyaltyProperties = new LoyaltyProperties();
    private final VolumeDiscountPolicy policy = new VolumeDiscountPolicy(loyaltyProperties);
    private final Client client = Client.builder().build();

    @Test
    void noDiscount_belowFirstThreshold() {
        BigDecimal discount = policy.calculateDiscount(client, BigDecimal.valueOf(500), false);

        assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void fifteenPercent_atFirstThreshold() {
        BigDecimal discount = policy.calculateDiscount(client, BigDecimal.valueOf(1000), false);

        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    void twentyPercent_atSecondThreshold() {
        BigDecimal discount = policy.calculateDiscount(client, BigDecimal.valueOf(1500), false);

        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void noDiscount_whenOwnQuestionEvenAboveThreshold() {
        BigDecimal discount = policy.calculateDiscount(client, BigDecimal.valueOf(1500), true);

        assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
