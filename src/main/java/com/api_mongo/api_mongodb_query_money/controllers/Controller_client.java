package com.api_mongo.api_mongodb_query_money.controllers;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api_mongo.api_mongodb_query_money.dtos.Dtos_cliente_create;
import com.api_mongo.api_mongodb_query_money.dtos.Dtos_cliente_login;
import com.api_mongo.api_mongodb_query_money.models.Models_client_create;
import com.api_mongo.api_mongodb_query_money.services.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/client")
public class Controller_client {

    private final Services_clients services_clients;
    private final Services_token services_token;

    public Controller_client(Services_clients services_clients, Services_token services_token) {
        this.services_clients = services_clients;
        this.services_token = services_token;
    }

    @GetMapping
    public ResponseEntity<Object> AllClients() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(services_clients.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> ClientById(@PathVariable(value = "id") String id) {

        Optional<Models_client_create> clientModelOptional = services_clients.findClientForId(id);
        if (clientModelOptional != null) {
            return ResponseEntity.status(HttpStatus.FOUND).body(clientModelOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Object> Login(@RequestBody @Valid Dtos_cliente_login dtos_cliente_login) {
        Models_client_create client = services_clients.getClientForEmailAndPasswordEquals(dtos_cliente_login);
        if (client != null) {
            System.out.println(services_token.generateToken(client));
            return ResponseEntity.status(HttpStatus.FOUND).body(services_token.generateToken(client));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> CreateAccount(@RequestBody @Valid Dtos_cliente_create dtos_cliente_create) {
        if (services_clients.verifyEmailExist(dtos_cliente_create.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(" Email already exists ");

        } else {
            System.out.println("Create the user: " + dtos_cliente_create.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(services_clients.saveNewClients(dtos_cliente_create));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> ModifyClientForId(@PathVariable(value = "id") String id,
            @RequestBody @Valid Dtos_cliente_create dtos_cliente) {
        Models_client_create clientModel = services_clients.putClients(id, dtos_cliente);
        if (clientModel != null) {
            return ResponseEntity.status(HttpStatus.FOUND).body(clientModel.getName() + " Is Changed");

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> DeleteClientForId(@PathVariable(value = "id") String id) {

        Models_client_create clientModel = services_clients.deleteClients(id);
        if (clientModel != null) {
            return ResponseEntity.status(HttpStatus.FOUND).body(clientModel.getName() + " is Deleted !");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }
    }

}
