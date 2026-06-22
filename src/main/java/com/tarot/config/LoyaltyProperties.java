package com.tarot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Бизнес-правила программы лояльности — настраиваются через application.properties без пересборки кода (OCP).
@Component
@ConfigurationProperties(prefix = "loyalty")
public class LoyaltyProperties {

    private int pointsPerSession = 10;
    private int pointsForDiscount = 50;
    private int discountPercent = 15;
    private int regularStatusThreshold = 3;
    private int vipStatusThreshold = 10;

    public int getPointsPerSession() {
        return pointsPerSession;
    }

    public void setPointsPerSession(int pointsPerSession) {
        this.pointsPerSession = pointsPerSession;
    }

    public int getPointsForDiscount() {
        return pointsForDiscount;
    }

    public void setPointsForDiscount(int pointsForDiscount) {
        this.pointsForDiscount = pointsForDiscount;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getRegularStatusThreshold() {
        return regularStatusThreshold;
    }

    public void setRegularStatusThreshold(int regularStatusThreshold) {
        this.regularStatusThreshold = regularStatusThreshold;
    }

    public int getVipStatusThreshold() {
        return vipStatusThreshold;
    }

    public void setVipStatusThreshold(int vipStatusThreshold) {
        this.vipStatusThreshold = vipStatusThreshold;
    }
}
