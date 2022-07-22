package com.api_mongo.api_mongodb_query_money.services;

import com.api_mongo.api_mongodb_query_money.models.Models_data_b3_names;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_data_b3_names;
import org.springframework.stereotype.Service;

@Service
public class Services_data_b3_names {

    private final Repository_data_b3_names repository;
    
    public Services_data_b3_names(Repository_data_b3_names repository) {
        this.repository = repository;
    }


    public void saveNewData(Models_data_b3_names models_data_b3_names) {
            repository.save(models_data_b3_names);
    }

}
