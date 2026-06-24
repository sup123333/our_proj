package com.tarot.mapper;

import com.tarot.dto.client.ClientResponse;
import com.tarot.entity.Client;
import com.tarot.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final SessionRepository sessionRepository;

    public ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getContact(),
                client.getPhone(),
                client.getTelegram(),
                client.getStatus(),
                client.getTotalPoints(),
                sessionRepository.countCompletedByClient(client.getId()),
                client.getCreatedAt(),
                client.getLastSessionAt()
        );
    }
}
