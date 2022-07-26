package com.api_mongo.api_mongodb_query_money.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api_mongo.api_mongodb_query_money.services.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/actionnames")
public class Controller_list_names extends Thread {

    private final Services_data_list_names services_data_list_names;

    public Controller_list_names(Services_data_list_names services_data_list_names) {
        this.services_data_list_names = services_data_list_names;
    }

    @GetMapping("")
    public ResponseEntity<Object> GetAllList() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(services_data_list_names.getAllNames());
    }

}
