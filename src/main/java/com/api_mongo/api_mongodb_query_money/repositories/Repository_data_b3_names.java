package com.api_mongo.api_mongodb_query_money.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.api_mongo.api_mongodb_query_money.models.Models_data_b3_names;

public interface Repository_data_b3_names extends MongoRepository<Models_data_b3_names, UUID> {

    Optional<Models_data_b3_names> findByNameAction(String nameAction);

}
