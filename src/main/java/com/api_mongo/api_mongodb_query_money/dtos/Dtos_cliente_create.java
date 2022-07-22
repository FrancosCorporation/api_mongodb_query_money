package com.api_mongo.api_mongodb_query_money.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Dtos_cliente_create {
    

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    @Size(min = 5)
    private String password;

    @NotBlank
    @NotNull
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
