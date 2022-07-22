package com.api_mongo.api_mongodb_query_money.controllers;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.tomcat.util.http.fileupload.IOUtils;
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

import com.api_mongo.api_mongodb_query_money.models.Models_layout_data_client;
import com.api_mongo.api_mongodb_query_money.services.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/wallet")
public class Controller_layout_client {

    private final Services_layout_data_client services_layout_data_client;
    // Models_layout_data_client

    public Controller_layout_client(Services_layout_data_client services_layout_data_client) {
        this.services_layout_data_client = services_layout_data_client;
    }

    @GetMapping
    public ResponseEntity<Object> AllLayouts() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(services_layout_data_client.getAllLayouts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> ClientById(@PathVariable(value = "id") String id) {

        Optional<Models_layout_data_client> models_layout_data_client = services_layout_data_client.findLayoutById(id);
        if (models_layout_data_client.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).body(models_layout_data_client.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }

    }

    @PostMapping("/register")
    public ResponseEntity<Object> CreateAccount(@RequestBody HttpServletRequest request , Models_layout_data_client models_layout_data_client) throws IOException {

        System.out.println("json = " + request.getScheme());
        return null;

    }
}