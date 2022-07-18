package com.api_mongo.api_mongodb_query_money.repositories;

import org.springframework.stereotype.Repository;

import com.api_mongo.api_mongodb_query_money.models.Client_create_model;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface Repository_Clients extends MongoRepository<Client_create_model, UUID> {
    

}
