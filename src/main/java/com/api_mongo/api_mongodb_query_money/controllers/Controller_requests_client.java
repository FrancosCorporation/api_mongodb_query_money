package com.api_mongo.api_mongodb_query_money.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
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

import com.api_mongo.api_mongodb_query_money.dtos.Dtos_cliente;
import com.api_mongo.api_mongodb_query_money.models.Client_model;
import com.api_mongo.api_mongodb_query_money.services.Services_global;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/client")
public class Controller_requests_client {

    final Services_global services_global;

    public Controller_requests_client(Services_global service_global) {
        this.services_global = service_global;
    }

    @GetMapping
    public ResponseEntity<Object> ReturnAllClients() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(services_global.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> ReturnClientById(@PathVariable(value = "id") String id) {
       Optional<Client_model> clientModelOptional = services_global.findClientForId(id);
        if (clientModelOptional != null) {
            return ResponseEntity.status(HttpStatus.FOUND).body(clientModelOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }

    }

    @PostMapping
    public ResponseEntity<Object> CreateAccount(@RequestBody @Valid Dtos_cliente dtos_cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(services_global.saveNewClients(dtos_cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> ModifyClientForId(@PathVariable(value = "id") String id,
            @RequestBody @Valid Dtos_cliente dtos_cliente) {
        Client_model clientModel= services_global.putClients(id, dtos_cliente);
        if (clientModel != null) {
            return ResponseEntity.status(HttpStatus.FOUND).body(clientModel.getName() + " Is Changed");

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> DeleteClientForId(@PathVariable(value = "id") String id) {
        
            Client_model clientModel = services_global.deleteClients(id);
            if (clientModel != null) {
                return ResponseEntity.status(HttpStatus.FOUND).body(clientModel.getName() + " is Deleted !");
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
            }
    }

}
