package com.tarot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Анонимная заявка с публичной формы лендинга — до того как стать настоящим Client/Session.
@Entity
@Table(name = "leads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String contact; // телефон или telegram

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service; // может быть не выбрана

    @Column(length = 2000)
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum LeadStatus {
        NEW,        // только пришла, тарологу нужно связаться
        CONTACTED   // тарологом обработана (вручную заводит Client/Session)
    }
}
