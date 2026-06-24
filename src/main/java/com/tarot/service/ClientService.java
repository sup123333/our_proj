package com.tarot.service;

import com.tarot.entity.Client;

import java.util.List;

public interface ClientService {

    Client getByContact(String contact);

    Client getById(Long id);

    List<Client> getAll();
}
