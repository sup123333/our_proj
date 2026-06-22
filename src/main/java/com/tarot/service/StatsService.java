package com.tarot.service;

import com.tarot.dto.stats.DashboardResponse;

import java.time.LocalDateTime;

public interface StatsService {

    DashboardResponse getDashboard(LocalDateTime from, LocalDateTime to);
}
