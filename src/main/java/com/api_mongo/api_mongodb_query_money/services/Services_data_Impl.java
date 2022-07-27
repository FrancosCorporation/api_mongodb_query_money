package com.api_mongo.api_mongodb_query_money.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.api_mongo.api_mongodb_query_money.models.Models_client_create;
import com.api_mongo.api_mongodb_query_money.models.Models_client_details;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_Clients;

@Service
public class Services_data_Impl implements UserDetailsService {
    private final Repository_Clients repository;

    public Services_data_Impl(Repository_Clients repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Models_client_create> usuario = repository.findByEmail(email);
        if (usuario.isPresent()) {
            throw new UsernameNotFoundException("Usuário [" + email + "] não encontrado");
        }

        return new Models_client_details(usuario);
    }

}
