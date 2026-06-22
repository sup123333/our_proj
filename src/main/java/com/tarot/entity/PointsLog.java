package com.tarot.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PointsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session; // null если баллы начислены/списаны вручную

    // Положительное = начисление, отрицательное = списание
    @Column(nullable = false)
    private Integer pointsDelta;

    @Column(nullable = false)
    private Integer balanceAfter; // баланс после операции

    @Column(nullable = false)
    private String reason; // "Оплата сеанса", "Списание скидки", "Ручное начисление"

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
