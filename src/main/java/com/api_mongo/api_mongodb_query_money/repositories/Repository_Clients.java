package com.api_mongo.api_mongodb_query_money.repositories;

import org.springframework.stereotype.Repository;

import com.api_mongo.api_mongodb_query_money.models.Models_client_create;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
interface Repository_clients extends MongoRepository<Models_client_create, UUID> {

    Optional<Models_client_create> findByEmail(String email);

}
