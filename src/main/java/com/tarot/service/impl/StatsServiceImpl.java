package com.tarot.service.impl;

import com.tarot.dto.stats.DashboardResponse;
import com.tarot.entity.Client;
import com.tarot.repository.ClientRepository;
import com.tarot.repository.SessionRepository;
import com.tarot.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final ClientRepository clientRepository;
    private final SessionRepository sessionRepository;

    @Override
    public DashboardResponse getDashboard(LocalDateTime from, LocalDateTime to) {
        long newClients = clientRepository.countNewClients(from, to);
        long churnedClients = clientRepository.findInactiveClients(
                LocalDateTime.now().minusDays(30)).size();
        long completedCount = sessionRepository.countCompletedInPeriod(from, to);
        BigDecimal revenue = sessionRepository.revenueInPeriod(from, to);

        Map<Client.ClientStatus, Long> byStatus = clientRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Client::getStatus, Collectors.counting()));

        List<DashboardResponse.PopularService> popular = sessionRepository.popularServices()
                .stream()
                .map(row -> new DashboardResponse.PopularService((String) row[0], (long) row[1]))
                .collect(Collectors.toList());

        return new DashboardResponse(newClients, churnedClients, completedCount, revenue, byStatus, popular);
    }
}
