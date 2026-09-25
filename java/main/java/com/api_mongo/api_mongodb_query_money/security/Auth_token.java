package com.api_mongo.api_mongodb_query_money.security;

import java.util.Date;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class Auth_token {
    private final int TIME_EXPIRATION = 86400000;
    final String TOKEN_SENHA = "6d79be3b-d3e7-461b-9803-0ee869959df6";

     public String generateToken(String email, String id ) {
        try {
            String Token = JWT.create().withSubject(email)
                    .withJWTId(id)
                    .withExpiresAt(new Date(System.currentTimeMillis() + TIME_EXPIRATION))
                    .sign(Algorithm.HMAC512(TOKEN_SENHA));
            return Token;
        } catch (Exception e) {
            return null;
        }
    }
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        return encode;
    }

}
