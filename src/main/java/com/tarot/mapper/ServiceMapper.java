package com.tarot.mapper;

import com.tarot.dto.service.ServiceResponse;
import com.tarot.entity.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceResponse toResponse(Service service) {
        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getPointsReward(),
                service.getActive()
        );
    }
}
