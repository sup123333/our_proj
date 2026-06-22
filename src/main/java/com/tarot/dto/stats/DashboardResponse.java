package com.tarot.dto.stats;

import com.tarot.entity.Client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long newClients,
        long churnedClients,
        long completedSessions,
        BigDecimal revenue,
        Map<Client.ClientStatus, Long> clientsByStatus,
        List<PopularService> popularServices
) {
    public record PopularService(String serviceName, long count) {
    }
}
