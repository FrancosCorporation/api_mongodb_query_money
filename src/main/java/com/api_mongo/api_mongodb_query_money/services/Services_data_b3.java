package com.api_mongo.api_mongodb_query_money.services;

import org.springframework.stereotype.Service;
import com.api_mongo.api_mongodb_query_money.models.Models_data_b3;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_data_b3;

@Service
public class Services_data_b3 {

    private final Repository_data_b3 repository;

    public Services_data_b3(Repository_data_b3 repository) {
        this.repository = repository;
    }

    public void saveNewData(Models_data_b3 models_data_b3) {
            repository.save(models_data_b3);
    }

}
