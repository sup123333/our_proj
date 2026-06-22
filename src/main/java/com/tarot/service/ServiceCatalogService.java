package com.tarot.service;

import com.tarot.dto.service.ServiceRequest;
import com.tarot.entity.Service;

import java.util.List;

public interface ServiceCatalogService {

    List<Service> listActive();

    List<Service> listAll();

    Service getById(Long id);

    Service create(ServiceRequest request);

    Service update(Long id, ServiceRequest request);

    void delete(Long id);
}
