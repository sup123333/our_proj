package com.tarot.controller;

import com.tarot.dto.session.SessionCreateRequest;
import com.tarot.dto.session.SessionResponse;
import com.tarot.entity.Client;
import com.tarot.mapper.SessionMapper;
import com.tarot.security.SecurityUtils;
import com.tarot.service.ClientService;
import com.tarot.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final ClientService clientService;
    private final SessionMapper sessionMapper;

    @PostMapping("/api/sessions")
    public ResponseEntity<SessionResponse> create(@Valid @RequestBody SessionCreateRequest request) {
        // clientId никогда не берётся из request body — только из аутентифицированного principal,
        // иначе клиент мог бы создавать сеансы от имени чужого аккаунта (IDOR).
        Client client = clientService.getByContact(SecurityUtils.currentContact());
        var session = sessionService.createSession(
                client.getId(), request.serviceId(), request.questionCount(), request.question(),
                request.usePoints(), request.ownQuestion());
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionMapper.toResponse(session));
    }

    @GetMapping("/api/sessions/me")
    public List<SessionResponse> mySessions() {
        Client client = clientService.getByContact(SecurityUtils.currentContact());
        return sessionService.getClientSessions(client.getId()).stream().map(sessionMapper::toResponse).toList();
    }

    @GetMapping("/api/admin/sessions/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SessionResponse> pending() {
        return sessionService.getPendingSessions().stream().map(sessionMapper::toResponse).toList();
    }

    @PostMapping("/api/admin/sessions/{id}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    public SessionResponse markAsPaid(@PathVariable Long id) {
        return sessionMapper.toResponse(sessionService.markAsPaid(id));
    }

    @PostMapping("/api/admin/sessions/{id}/mark-done")
    @PreAuthorize("hasRole('ADMIN')")
    public SessionResponse markAsDone(@PathVariable Long id) {
        return sessionMapper.toResponse(sessionService.markAsDone(id));
    }
}
