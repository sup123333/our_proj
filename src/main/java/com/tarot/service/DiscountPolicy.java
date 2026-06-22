package com.tarot.service;

import com.tarot.entity.Client;

import java.math.BigDecimal;

// Стратегия расчёта скидки — новое правило скидки = новая реализация, без изменения вызывающего кода (OCP).
public interface DiscountPolicy {

    BigDecimal calculateDiscount(Client client, BigDecimal price);
}
