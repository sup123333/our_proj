package com.tarot.mapper;

import com.tarot.dto.lead.LeadResponse;
import com.tarot.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getName(),
                lead.getContact(),
                lead.getService() != null ? lead.getService().getId() : null,
                lead.getService() != null ? lead.getService().getName() : null,
                lead.getQuestion(),
                lead.getStatus(),
                lead.getCreatedAt()
        );
    }
}
