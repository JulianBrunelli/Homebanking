package com.mindhub.homebanking.service;


import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {

    void save(Client client);

    Client findByEmail(String email);

    ClientDTO getClientDTO(String email);

    List<ClientDTO> getClientsDTO();

    ClientDTO findById(long id);

}
