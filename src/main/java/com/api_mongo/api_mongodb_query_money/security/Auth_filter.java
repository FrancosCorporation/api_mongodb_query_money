package com.api_mongo.api_mongodb_query_money.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class Auth_filter extends UsernamePasswordAuthenticationFilter {

    final AuthenticationManager authmanager;

    public Auth_filter(AuthenticationManager authmanager) {
        this.authmanager = authmanager;
    }
    

}
