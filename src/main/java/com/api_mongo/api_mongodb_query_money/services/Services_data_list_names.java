package com.api_mongo.api_mongodb_query_money.services;

import com.api_mongo.api_mongodb_query_money.models.Models_data_list_names;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_data_list_names;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class Services_data_list_names {

    private final Repository_data_list_names repository;

    public Services_data_list_names(Repository_data_list_names repository) {
        this.repository = repository;
    }

    public void saveNewList(Models_data_list_names models_data_list_names) {
        repository.save(models_data_list_names);
    }

    public List<Models_data_list_names> getAllNames() {
        return repository.findAll();
    }

}
