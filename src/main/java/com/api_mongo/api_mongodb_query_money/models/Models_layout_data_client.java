package com.api_mongo.api_mongodb_query_money.models;

import java.util.ArrayList;
import java.util.UUID;

import javax.validation.constraints.*;

import org.springframework.data.annotation.*;

public class Models_layout_data_client {
    @Id
    @NotBlank
    @NotNull
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID id;
    
    @NotBlank
    @NotNull
    private ArrayList<Models_layout_data_client_list> actionsAndPrice ;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ArrayList<Models_layout_data_client_list> getActionsAndPrice() {
        return actionsAndPrice;
    }

    public void setActionsAndPrice(ArrayList<Models_layout_data_client_list> actionsAndPrice) {
        this.actionsAndPrice = actionsAndPrice;
    }
    
}
