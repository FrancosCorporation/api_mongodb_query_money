package com.api_mongo.api_mongodb_query_money.repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.api_mongo.api_mongodb_query_money.models.Models_data_b3_names;

@Repository
interface Repository_data_b3_names extends MongoRepository<Models_data_b3_names, String> {

    List<Models_data_b3_names> findByNameAction(String nameAction);
}
