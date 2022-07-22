package com.api_mongo.api_mongodb_query_money.repositories;

import org.springframework.stereotype.Repository;

import com.api_mongo.api_mongodb_query_money.models.Models_data_b3;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface Repository_data_b3 extends MongoRepository<Models_data_b3, UUID> {

    Optional<Models_data_b3> findByDate(String date);
}
