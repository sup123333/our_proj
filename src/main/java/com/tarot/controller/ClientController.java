package com.tarot.controller;

import com.tarot.dto.client.ClientResponse;
import com.tarot.mapper.ClientMapper;
import com.tarot.security.SecurityUtils;
import com.tarot.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @GetMapping("/clients/me")
    public ClientResponse me() {
        return clientMapper.toResponse(clientService.getByContact(SecurityUtils.currentContact()));
    }

    @GetMapping("/admin/clients")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClientResponse> listAll() {
        return clientService.getAll().stream().map(clientMapper::toResponse).toList();
    }
}
