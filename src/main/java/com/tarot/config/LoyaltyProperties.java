package com.tarot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Бизнес-правила программы лояльности — настраиваются через application.properties без пересборки кода (OCP).
@Component
@ConfigurationProperties(prefix = "loyalty")
public class LoyaltyProperties {

    // Имя каталожной услуги для "своего вопроса" — её цена в каталоге трактуется как цена за один вопрос.
    public static final String OWN_QUESTION_SERVICE_NAME = "Свой вопрос";

    private int pointsPerSession = 10;
    private int pointsForDiscount = 50;
    private int discountPercent = 15;
    private int ownQuestionDiscountPercent = 10;
    private int regularStatusThreshold = 3;
    private int vipStatusThreshold = 10;

    // Скидка за объём — только для тем "её карточки" (не для своих вопросов), начисляется автоматически
    private java.math.BigDecimal volumeThreshold1 = new java.math.BigDecimal("1000");
    private int volumePercent1 = 15;
    private java.math.BigDecimal volumeThreshold2 = new java.math.BigDecimal("1500");
    private int volumePercent2 = 20;

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

    public int getOwnQuestionDiscountPercent() {
        return ownQuestionDiscountPercent;
    }

    public void setOwnQuestionDiscountPercent(int ownQuestionDiscountPercent) {
        this.ownQuestionDiscountPercent = ownQuestionDiscountPercent;
    }

    public java.math.BigDecimal getVolumeThreshold1() {
        return volumeThreshold1;
    }

    public void setVolumeThreshold1(java.math.BigDecimal volumeThreshold1) {
        this.volumeThreshold1 = volumeThreshold1;
    }

    public int getVolumePercent1() {
        return volumePercent1;
    }

    public void setVolumePercent1(int volumePercent1) {
        this.volumePercent1 = volumePercent1;
    }

    public java.math.BigDecimal getVolumeThreshold2() {
        return volumeThreshold2;
    }

    public void setVolumeThreshold2(java.math.BigDecimal volumeThreshold2) {
        this.volumeThreshold2 = volumeThreshold2;
    }

    public int getVolumePercent2() {
        return volumePercent2;
    }

    public void setVolumePercent2(int volumePercent2) {
        this.volumePercent2 = volumePercent2;
    }
}
