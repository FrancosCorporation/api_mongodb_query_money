package com.api_mongo.api_mongodb_query_money.repositories;

import org.springframework.stereotype.Repository;
import com.api_mongo.api_mongodb_query_money.models.Models_layout_data_client;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface Repository_layout_data_client extends MongoRepository<Models_layout_data_client, UUID> {
    
    Optional<Models_layout_data_client> findById(UUID uuid);

}
