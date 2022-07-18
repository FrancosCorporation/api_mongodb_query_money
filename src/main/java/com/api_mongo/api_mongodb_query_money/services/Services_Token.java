package com.api_mongo.api_mongodb_query_money.services;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api_mongo.api_mongodb_query_money.models.Models_client_create;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class Services_token {
    public static final int TIME_EXPIRATION = 86400000;
    public static final String TOKEN_SENHA = "6d79be3b-d3e7-461b-9803-0ee869959df6";

    @Bean
    public String generateToken(Models_client_create model_client_create) {
        try {
            String Token = JWT.create().withSubject(model_client_create.getEmail())
                    .withSubject(model_client_create.getId().toString())
                    .withExpiresAt(new Date(System.currentTimeMillis() + TIME_EXPIRATION))
                    .sign(Algorithm.HMAC512(TOKEN_SENHA));
            return Token;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        return encode;
    }
    
}
