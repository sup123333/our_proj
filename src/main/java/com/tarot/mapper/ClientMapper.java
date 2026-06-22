package com.tarot.mapper;

import com.tarot.dto.client.ClientResponse;
import com.tarot.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getTelegram(),
                client.getStatus(),
                client.getTotalPoints(),
                client.getCreatedAt(),
                client.getLastSessionAt()
        );
    }
}
