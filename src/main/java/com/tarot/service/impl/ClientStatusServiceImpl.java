package com.tarot.service.impl;

import com.tarot.config.LoyaltyProperties;
import com.tarot.entity.Client;
import com.tarot.repository.ClientRepository;
import com.tarot.service.ClientStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClientStatusServiceImpl implements ClientStatusService {

    private final ClientRepository clientRepository;
    private final LoyaltyProperties loyaltyProperties;

    @Override
    @Transactional
    public void updateStatus(Client client, long completedSessions) {
        Client.ClientStatus newStatus;
        if (completedSessions >= loyaltyProperties.getVipStatusThreshold()) {
            newStatus = Client.ClientStatus.VIP;
        } else if (completedSessions >= loyaltyProperties.getRegularStatusThreshold()) {
            newStatus = Client.ClientStatus.REGULAR;
        } else {
            newStatus = Client.ClientStatus.NEW;
        }

        if (client.getStatus() != newStatus) {
            client.setStatus(newStatus);
            clientRepository.save(client);
        }
    }
}
