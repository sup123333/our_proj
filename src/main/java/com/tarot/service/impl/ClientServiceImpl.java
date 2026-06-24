package com.tarot.service.impl;

import com.tarot.entity.Client;
import com.tarot.exception.ResourceNotFoundException;
import com.tarot.repository.ClientRepository;
import com.tarot.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client getByContact(String contact) {
        return clientRepository.findByContact(contact)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
    }

    @Override
    public Client getById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
    }

    @Override
    public List<Client> getAll() {
        return clientRepository.findAll();
    }
}
