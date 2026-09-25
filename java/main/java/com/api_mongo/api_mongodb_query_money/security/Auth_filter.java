package com.api_mongo.api_mongodb_query_money.security;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.api_mongo.api_mongodb_query_money.models.Models_client_create;
import com.api_mongo.api_mongodb_query_money.models.Models_client_details;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Auth_filter extends UsernamePasswordAuthenticationFilter {

    final AuthenticationManager authmanager;
    final Auth_token service_token;

  

    public Auth_filter(AuthenticationManager authmanager, Auth_token service_token) {
        this.authmanager = authmanager;
        this.service_token = service_token;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            Models_client_create client_create = new ObjectMapper().readValue(request.getInputStream(),
                    Models_client_create.class);
            return authmanager.authenticate(new UsernamePasswordAuthenticationToken(client_create.getEmail(),
                    client_create.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException("Falha ao autenticar usuario", e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        Models_client_details client_details = (Models_client_details) authResult.getPrincipal();
                System.out.println("Foi autenticado com sucesso.");
                response.getWriter().write(service_token.generateToken(client_details.getUsername(),client_details.getId().toString()));
                response.getWriter().flush();
    }

    

}
