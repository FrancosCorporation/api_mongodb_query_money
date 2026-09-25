package com.api_mongo.api_mongodb_query_money.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public class Dtos_cliente_login {
    

    @NotBlank
    @Size(min = 5)
    private String password;

    @NotBlank
    private String email;

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
