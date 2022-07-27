package com.api_mongo.api_mongodb_query_money.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.api_mongo.api_mongodb_query_money.models.Models_data_list_names;

@Repository
interface Repository_data_list_names extends MongoRepository<Models_data_list_names, String> {

}
