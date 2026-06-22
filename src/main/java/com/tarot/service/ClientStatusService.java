package com.tarot.service;

import com.tarot.entity.Client;

public interface ClientStatusService {

    void updateStatus(Client client, long completedSessions);
}
