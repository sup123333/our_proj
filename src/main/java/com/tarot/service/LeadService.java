package com.tarot.service;

import com.tarot.dto.lead.LeadRequest;
import com.tarot.entity.Lead;

import java.util.List;

public interface LeadService {

    Lead createLead(LeadRequest request);

    List<Lead> getNewLeads();

    Lead markContacted(Long id);
}
