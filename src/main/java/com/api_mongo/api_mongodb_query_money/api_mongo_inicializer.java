package com.api_mongo.api_mongodb_query_money;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class api_mongo_inicializer {

	public static void main(String[] args) {
		SpringApplication.run(api_mongo_inicializer.class, args);
	}

}
