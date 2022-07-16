package com.api_mongo.api_mongodb_query_money.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.api_mongo.api_mongodb_query_money.dtos.Dtos_cliente;
import com.api_mongo.api_mongodb_query_money.models.Client_model;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_global;

@Service
public class Services_global {

    final Repository_global repository_global;

    public Services_global(Repository_global repository_global) {
        this.repository_global = repository_global;
    }

    // Get ALL Clients
    public List<Client_model> getAllClients() {
        return repository_global.findAll();
    }

    // Find by id and Get Client
    public Optional<Client_model> findClientForId(String id) {
        try {
            Optional<Client_model> clientModelOptional = repository_global.findById(UUID.fromString(id));
            if (clientModelOptional.isPresent()) {
                return clientModelOptional;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Save the new Clients !
    public Client_model saveNewClients(Dtos_cliente dtos_cliente) {
        var clientModel = new Client_model();
        BeanUtils.copyProperties(dtos_cliente, clientModel);
        clientModel.setId(UUID.randomUUID());
        clientModel.setDataCreate(LocalDateTime.now(ZoneId.of("UTC")));
        clientModel.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
        return repository_global.save(clientModel);
    }

    // Find by id and Put Clients !
    public Client_model putClients(String id, Dtos_cliente dtos_cliente) {

        Optional<Client_model> clientModelOptional = findClientForId(id);
        if (clientModelOptional != null) {
            var clientModelLocal = clientModelOptional.get();
            BeanUtils.copyProperties(dtos_cliente, clientModelLocal);
            clientModelLocal.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
            repository_global.save(clientModelLocal);
            return clientModelLocal;
        } else {
            return null;
        }

    }

    // Find by id and Delete Clients !
    public Client_model deleteClients(String id) {
        Optional<Client_model> clientModelOptional = findClientForId(id);
        if (clientModelOptional != null) {
            repository_global.delete(clientModelOptional.get());
            return clientModelOptional.get();
        } else {
            return null;
        }

    }

}
