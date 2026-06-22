package com.tarot.service.impl;

import com.tarot.dto.service.ServiceRequest;
import com.tarot.entity.Service;
import com.tarot.exception.ResourceNotFoundException;
import com.tarot.repository.ServiceRepository;
import com.tarot.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    @Override
    public List<Service> listActive() {
        return serviceRepository.findByActiveTrue();
    }

    @Override
    public List<Service> listAll() {
        return serviceRepository.findAll();
    }

    @Override
    public Service getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Услуга не найдена"));
    }

    @Override
    @Transactional
    public Service create(ServiceRequest request) {
        Service service = Service.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .pointsReward(request.pointsReward() != null ? request.pointsReward() : 0)
                .active(request.active() == null || request.active())
                .build();
        return serviceRepository.save(service);
    }

    @Override
    @Transactional
    public Service update(Long id, ServiceRequest request) {
        Service service = getById(id);
        service.setName(request.name());
        service.setDescription(request.description());
        service.setPrice(request.price());
        if (request.pointsReward() != null) {
            service.setPointsReward(request.pointsReward());
        }
        if (request.active() != null) {
            service.setActive(request.active());
        }
        return serviceRepository.save(service);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Service service = getById(id);
        service.setActive(false);
        serviceRepository.save(service);
    }
}
