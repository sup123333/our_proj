package com.tarot.service.impl;

import com.tarot.dto.lead.LeadRequest;
import com.tarot.entity.Lead;
import com.tarot.entity.Service;
import com.tarot.exception.ResourceNotFoundException;
import com.tarot.repository.LeadRepository;
import com.tarot.repository.ServiceRepository;
import com.tarot.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional
    public Lead createLead(LeadRequest request) {
        Service service = null;
        if (request.serviceId() != null) {
            service = serviceRepository.findById(request.serviceId()).orElse(null);
        }

        Lead lead = Lead.builder()
                .name(request.name())
                .contact(request.contact())
                .service(service)
                .question(request.question())
                .build();

        return leadRepository.save(lead);
    }

    @Override
    public List<Lead> getNewLeads() {
        return leadRepository.findByStatusOrderByCreatedAtDesc(Lead.LeadStatus.NEW);
    }

    @Override
    @Transactional
    public Lead markContacted(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена"));
        lead.setStatus(Lead.LeadStatus.CONTACTED);
        return leadRepository.save(lead);
    }
}
