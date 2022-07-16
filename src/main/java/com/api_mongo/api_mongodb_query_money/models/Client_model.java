package com.api_mongo.api_mongodb_query_money.models;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.annotation.Id;

public class Client_model{

    @Id
    private UUID id;

    private String name;

    private String password;
    
    private String email;
    
    private LocalDateTime dataCreate;

    private LocalDateTime changeDate;



    public LocalDateTime getDataCreate() {
        return dataCreate;
    }

    public void setDataCreate(LocalDateTime dataCreate) {
        this.dataCreate = dataCreate;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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