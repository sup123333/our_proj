package com.tarot.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // цена на момент заявки (service.price может измениться)

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountApplied = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice; // price - discountApplied

    // true — клиент задаёт свой вопрос вместо темы из подобранных карточек ("её карточки");
    // на своих вопросах не действует автоматическая скидка за объём.
    @Column(nullable = false)
    @Builder.Default
    private Boolean ownQuestion = false;

    // discountApplied может включать и объёмную скидку, и скидку за баллы —
    // баллы реально списываются при оплате только если эта часть скидки не нулевая.
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal pointsDiscountApplied = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SessionStatus status = SessionStatus.PENDING;

    @Column(length = 2000)
    private String clientQuestion; // вопрос клиента при записи

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt;
    private LocalDateTime completedAt;

    // Сколько баллов начислено за этот сеанс
    @Builder.Default
    private Integer pointsEarned = 0;

    public enum SessionStatus {
        PENDING,    // заявка создана, ждёт оплаты
        PAID,       // оплачено
        DONE,       // сеанс проведён
        CANCELLED   // отменено
    }
}
