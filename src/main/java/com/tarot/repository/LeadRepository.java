package com.tarot.repository;

import com.tarot.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByStatusOrderByCreatedAtDesc(Lead.LeadStatus status);
}
