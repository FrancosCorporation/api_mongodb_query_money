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
import com.api_mongo.api_mongodb_query_money.repositories.Repository_Clients;
import com.api_mongo.api_mongodb_query_money.security.Auth_token;

import lombok.var;

@Service
public class Services_clients {

    private final Repository_Clients Repository_Clients;
    private final PasswordEncoder encoder;

    public Services_clients(Repository_Clients Repository_Clients, Auth_token service_token,
            PasswordEncoder encoder) {
        this.Repository_Clients = Repository_Clients;
        this.encoder = encoder;
    }

    // Get ALL Clients
    public List<Models_client_create> getAllClients() {
        return Repository_Clients.findAll();
    }

    // Find by id and Get Client
    public Optional<Models_client_create> findClientForId(UUID id) {
        try {
            Optional<Models_client_create> clientModelOptional = Repository_Clients.findById(id);
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

            Optional<Models_client_create> client = Repository_Clients.findByEmail(dtos_cliente_login.getEmail());
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
        var clientModel = new Models_client_create();
        BeanUtils.copyProperties(dtos_cliente, clientModel);
        clientModel.setId(UUID.randomUUID());
        clientModel.setDataCreate(LocalDateTime.now(ZoneId.of("UTC")));
        clientModel.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
        clientModel.setPassword(encoder.encode(clientModel.getPassword()));
        return Repository_Clients.save(clientModel);
    }

    // Find by id and Put Clients !
    public Models_client_create putClients(UUID id, Dtos_cliente_create dtos_cliente) {

        Optional<Models_client_create> clientModelOptional = findClientForId(id);
        if (clientModelOptional != null) {
            var clientModelLocal = clientModelOptional.get();
            BeanUtils.copyProperties(dtos_cliente, clientModelLocal);
            clientModelLocal.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
            Repository_Clients.save(clientModelLocal);
            return clientModelLocal;
        } else {
            return null;
        }

    }

    // Find by id and Delete Clients !
    public Models_client_create deleteClients(UUID id) {
        Optional<Models_client_create> clientModelOptional = findClientForId(id);
        if (clientModelOptional != null) {
            Repository_Clients.delete(clientModelOptional.get());
            return clientModelOptional.get();
        } else {
            return null;
        }

    }

    // Verify email exist !
    public boolean verifyEmailExist(String email) {
        if (!Repository_Clients.findByEmail(email).isPresent()) {
            return true;

        } else {
            return false;
        }

    }

}
