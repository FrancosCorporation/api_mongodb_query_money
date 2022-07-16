package com.api_mongo.api_mongodb_query_money.repositories;

import org.springframework.stereotype.Repository;

import com.api_mongo.api_mongodb_query_money.models.Client_model;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface Repository_global extends MongoRepository<Client_model, UUID> {

}
