package com.tarot.repository;

import com.tarot.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<Session> findByStatus(Session.SessionStatus status);

    @Query("select count(s) from Session s where s.client.id = :clientId and s.status = 'DONE'")
    long countCompletedByClient(@Param("clientId") Long clientId);

    @Query("select count(s) from Session s where s.status = 'DONE' and s.completedAt between :from and :to")
    long countCompletedInPeriod(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select coalesce(sum(s.finalPrice), 0) from Session s where s.status = 'DONE' and s.completedAt between :from and :to")
    BigDecimal revenueInPeriod(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select s.service.name, count(s) from Session s where s.status = 'DONE' group by s.service.name order by count(s) desc")
    List<Object[]> popularServices();
}
