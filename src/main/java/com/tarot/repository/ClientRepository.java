package com.tarot.repository;

import com.tarot.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    @Query("select count(c) from Client c where c.createdAt between :from and :to")
    long countNewClients(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select c from Client c where c.lastSessionAt is null or c.lastSessionAt < :since")
    List<Client> findInactiveClients(@Param("since") LocalDateTime since);
}
