package com.api_mongo.api_mongodb_query_money.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api_mongo.api_mongodb_query_money.dtos.Dtos_cliente_create;
import com.api_mongo.api_mongodb_query_money.dtos.Dtos_cliente_login;
import com.api_mongo.api_mongodb_query_money.models.Models_client_create;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_clients;
import com.api_mongo.api_mongodb_query_money.security.Auth_token;

@Service
public class Servicesclients {

    private final Repository_clients Repository_clients;
    private final PasswordEncoder encoder;

    public Servicesclients(Repository_clients Repository_clients, Auth_token service_token,
            PasswordEncoder encoder) {
        this.Repository_clients = Repository_clients;
        this.encoder = encoder;
    }

    // Get ALL Clients
    public List<Models_client_create> getAllClients() {
        return Repository_clients.findAll();
    }

    // Find by id and Get Client
    public Optional<Models_client_create> findClientForId(UUID id) {
        try {
            Optional<Models_client_create> clientModelOptional = Repository_clients.findById(id);
            if (clientModelOptional.isPresent()) {
                return clientModelOptional;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Models_client_create getClientForEmailAndPasswordEquals(Dtos_cliente_login dtos_cliente_login) {
        try {

            Optional<Models_client_create> client = Repository_clients.findByEmail(dtos_cliente_login.getEmail());
            if (encoder.matches(dtos_cliente_login.getPassword(), client.get().getPassword())) {
                return client.get();
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    // Save the new Clients !
    public Models_client_create saveNewClients(Dtos_cliente_create dtos_cliente) {
        Models_client_create clientModel = new Models_client_create();
        BeanUtils.copyProperties(dtos_cliente, clientModel);
        clientModel.setId(UUID.randomUUID());
        clientModel.setDataCreate(LocalDateTime.now(ZoneId.of("UTC")));
        clientModel.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
        clientModel.setPassword(encoder.encode(clientModel.getPassword()));
        return Repository_clients.save(clientModel);
    }

    // Find by id and Put Clients !
    public Models_client_create putClients(UUID id, Dtos_cliente_create dtos_cliente) {

        Optional<Models_client_create> clientModelOptional = findClientForId(id);
        if (clientModelOptional != null) {
            Models_client_create clientModelLocal = clientModelOptional.get();
            BeanUtils.copyProperties(dtos_cliente, clientModelLocal);
            clientModelLocal.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
            Repository_clients.save(clientModelLocal);
            return clientModelLocal;
        } else {
            return null;
        }

    }

    // Find by id and Delete Clients !
    public Models_client_create deleteClients(UUID id) {
        Optional<Models_client_create> clientModelOptional = findClientForId(id);
        if (clientModelOptional != null) {
            Repository_clients.delete(clientModelOptional.get());
            return clientModelOptional.get();
        } else {
            return null;
        }

    }

    // Verify email exist !
    public boolean verifyEmailExist(String email) {
        if (Repository_clients.findByEmail(email).isPresent()) {
            return true;

        } else {
            return false;
        }

    }

}
