package com.api_mongo.api_mongodb_query_money.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.api_mongo.api_mongodb_query_money.models.Models_layout_data_client;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_layout_data_client;

@Service
public class Services_layout_data_client {
    private final Repository_layout_data_client repository_layout_data_client;

    public Services_layout_data_client(Repository_layout_data_client repository_layout_data_client) {
        this.repository_layout_data_client = repository_layout_data_client;
    }

    public Optional<Models_layout_data_client> findLayoutById(String id) {
        try {
            Optional<Models_layout_data_client> models_layout_data_client = repository_layout_data_client.findById(UUID.fromString(id));
            if (models_layout_data_client.isPresent()) {
                return models_layout_data_client;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    public List<Models_layout_data_client> getAllLayouts() {
        return repository_layout_data_client.findAll();
    }

    public Models_layout_data_client saveNewLayout(Models_layout_data_client models_layout_data_client) {
        return repository_layout_data_client.save(models_layout_data_client);
    }

}
