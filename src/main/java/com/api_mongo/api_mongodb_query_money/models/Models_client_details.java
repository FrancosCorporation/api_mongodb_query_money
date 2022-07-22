package com.api_mongo.api_mongodb_query_money.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class Models_client_details implements UserDetails {

    final Optional<Models_client_create> client;

  
    
    public Models_client_details(Optional<Models_client_create> client) {
        this.client = client;
    }

    @Bean
    public UUID getId() {
        return client.orElse(new Models_client_create()).getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return client.orElse(new Models_client_create()).getPassword();
    }

    @Override
    public String getUsername() {
        return client.orElse(new Models_client_create()).getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
