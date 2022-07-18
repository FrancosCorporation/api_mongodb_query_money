package com.api_mongo.api_mongodb_query_money.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.api_mongo.api_mongodb_query_money.dtos.Dtos_cliente_create;
import com.api_mongo.api_mongodb_query_money.dtos.Dtos_cliente_login;
import com.api_mongo.api_mongodb_query_money.models.Client_create_model;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_Clients;

@Service
public class Services_Clients {

    final Repository_Clients repository_global;

    public Services_Clients(Repository_Clients repository_global) {
        this.repository_global = repository_global;
    }

    // Get ALL Clients
    public List<Client_create_model> getAllClients() {
        return repository_global.findAll();
    }

    // Find by id and Get Client
    public Optional<Client_create_model> findClientForId(String id) {
        try {
            Optional<Client_create_model> clientModelOptional = repository_global.findById(UUID.fromString(id));
            if (clientModelOptional.isPresent()) {
                clientModelOptional.get().setPassword(null);
                clientModelOptional.get().setId(null);
                return clientModelOptional;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Find by Email and Get Client
    public Client_create_model findClientForEmail(Dtos_cliente_login dtos_cliente_login) {
        try {
            List<Client_create_model> listClients = getAllClients();
            for (Client_create_model client_create_model : listClients) {
                if (client_create_model.getEmail().equals(dtos_cliente_login.getEmail())) {
                    return client_create_model;
                }
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    public Client_create_model GetClientForEmailAndPasswordEquals(Dtos_cliente_login dtos_cliente_login,
            boolean DadosOfforNo) {
        try {
            if (DadosOfforNo) {

                Client_create_model client = findClientForEmail(dtos_cliente_login);
                if (client.getPassword().equals(dtos_cliente_login.getPassword())) {
                    client.setPassword(null);
                    client.setId(null);
                    return client;
                } else {
                    return null;
                }
            } else {
                Client_create_model client = findClientForEmail(dtos_cliente_login);
                if (client.getPassword().equals(dtos_cliente_login.getPassword())) {
                    return client;
                } else {
                    return null;
                }

            }
        } catch (Exception e) {
            return null;
        }
    }

    // Save the new Clients !
    public Client_create_model saveNewClients(Dtos_cliente_create dtos_cliente) {
        var clientModel = new Client_create_model();
        BeanUtils.copyProperties(dtos_cliente, clientModel);
        clientModel.setId(UUID.randomUUID());
        clientModel.setDataCreate(LocalDateTime.now(ZoneId.of("UTC")));
        clientModel.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
        return repository_global.save(clientModel);
    }

    // Find by id and Put Clients !
    public Client_create_model putClients(String id, Dtos_cliente_create dtos_cliente) {

        Optional<Client_create_model> clientModelOptional = findClientForId(id);
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
    public Client_create_model deleteClients(String id) {
        Optional<Client_create_model> clientModelOptional = findClientForId(id);
        if (clientModelOptional != null) {
            repository_global.delete(clientModelOptional.get());
            return clientModelOptional.get();
        } else {
            return null;
        }

    }

    public boolean verifyEmailExist(String email) {
        List<Client_create_model> list = repository_global.findAll();
        for (Client_create_model client_create_model : list) {
            if (client_create_model.getEmail().equals(email)) {
                return true;
            }
        }
        return false;

    }
}
