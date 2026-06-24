package com.tarot.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Логин — нормализованный телефон или telegram (без email: клиент его не запомнит)
    @Column(nullable = false, unique = true)
    private String contact;

    @Column(nullable = false)
    private String password;

    private String phone;
    private String telegram;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ClientStatus status = ClientStatus.NEW;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastSessionAt;

    public enum ClientStatus {
        NEW,        // первый визит или ещё не платил
        REGULAR,    // 3+ оплаченных сеанса
        VIP         // 10+ оплаченных сеанса
    }
}
