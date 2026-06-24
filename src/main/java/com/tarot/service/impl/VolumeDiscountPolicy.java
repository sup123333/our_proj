package com.tarot.service.impl;

import com.tarot.config.LoyaltyProperties;
import com.tarot.entity.Client;
import com.tarot.service.DiscountPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Скидка за объём — автоматическая, без баллов. Действует только на темах из подобранных карточек
// ("её карточки"); свои вопросы клиент платит по полной цене за вопрос, без скидки за объём.
@Component
@RequiredArgsConstructor
public class VolumeDiscountPolicy implements DiscountPolicy {

    private final LoyaltyProperties loyaltyProperties;

    @Override
    public BigDecimal calculateDiscount(Client client, BigDecimal price, boolean ownQuestion) {
        if (ownQuestion) {
            return BigDecimal.ZERO;
        }
        int percent;
        if (price.compareTo(loyaltyProperties.getVolumeThreshold2()) >= 0) {
            percent = loyaltyProperties.getVolumePercent2();
        } else if (price.compareTo(loyaltyProperties.getVolumeThreshold1()) >= 0) {
            percent = loyaltyProperties.getVolumePercent1();
        } else {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100));
    }
}
