package com.tarot.controller;

import com.tarot.dto.service.ServiceRequest;
import com.tarot.dto.service.ServiceResponse;
import com.tarot.mapper.ServiceMapper;
import com.tarot.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;
    private final ServiceMapper serviceMapper;

    @GetMapping("/api/services")
    public List<ServiceResponse> listActive() {
        return serviceCatalogService.listActive().stream().map(serviceMapper::toResponse).toList();
    }

    @GetMapping("/api/admin/services")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ServiceResponse> listAll() {
        return serviceCatalogService.listAll().stream().map(serviceMapper::toResponse).toList();
    }

    @PostMapping("/api/admin/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody ServiceRequest request) {
        var created = serviceCatalogService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceMapper.toResponse(created));
    }

    @PutMapping("/api/admin/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceResponse update(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        return serviceMapper.toResponse(serviceCatalogService.update(id, request));
    }

    @DeleteMapping("/api/admin/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceCatalogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
