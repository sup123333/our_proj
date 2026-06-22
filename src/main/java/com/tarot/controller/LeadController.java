package com.tarot.controller;

import com.tarot.dto.lead.LeadRequest;
import com.tarot.dto.lead.LeadResponse;
import com.tarot.mapper.LeadMapper;
import com.tarot.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;
    private final LeadMapper leadMapper;

    // Публичный, неаутентифицированный — форма записи на лендинге дёргает именно этот эндпоинт.
    @PostMapping("/api/leads")
    public ResponseEntity<LeadResponse> create(@Valid @RequestBody LeadRequest request) {
        var lead = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(leadMapper.toResponse(lead));
    }

    @GetMapping("/api/admin/leads")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeadResponse> listNew() {
        return leadService.getNewLeads().stream().map(leadMapper::toResponse).toList();
    }

    @PostMapping("/api/admin/leads/{id}/mark-contacted")
    @PreAuthorize("hasRole('ADMIN')")
    public LeadResponse markContacted(@PathVariable Long id) {
        return leadMapper.toResponse(leadService.markContacted(id));
    }
}
