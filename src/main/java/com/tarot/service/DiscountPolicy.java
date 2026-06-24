package com.tarot.service;

import com.tarot.entity.Client;

import java.math.BigDecimal;

// Стратегия расчёта скидки — новое правило скидки = новая реализация, без изменения вызывающего кода (OCP).
public interface DiscountPolicy {

    // ownQuestion: true — клиент задаёт свой вопрос вместо темы из подобранных карточек ("её карточки").
    BigDecimal calculateDiscount(Client client, BigDecimal price, boolean ownQuestion);
}
