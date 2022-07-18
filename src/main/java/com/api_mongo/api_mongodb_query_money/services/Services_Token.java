package com.api_mongo.api_mongodb_query_money.services;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.api_mongo.api_mongodb_query_money.models.Client_create_model;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class Services_Token {
    public static final int TIME_EXPIRATION = 86400000;
    public static final String TOKEN_SENHA = "6d79be3b-d3e7-461b-9803-0ee869959df6";

    public String generateToken(Client_create_model client_create_model) {
        try {
            String Token = JWT.create().withSubject(client_create_model.getEmail())
                    .withSubject(client_create_model.getId().toString())
                    .withExpiresAt(new Date(System.currentTimeMillis() + TIME_EXPIRATION))
                    .sign(Algorithm.HMAC512(TOKEN_SENHA));
            return Token;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
