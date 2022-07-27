package com.api_mongo.api_mongodb_query_money.models;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Wallet")
public class Models_layout_data_client {

    @Id
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID id;

    private List<Models_layout_data_client_list> actionsAndPrice;

    public Models_layout_data_client() {

    }

    public Models_layout_data_client(UUID id, List<Models_layout_data_client_list> actionsAndPrice) {
        this.id = id;
        this.actionsAndPrice = actionsAndPrice;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Models_layout_data_client_list> getActionsAndPrice() {
        return actionsAndPrice;
    }

    public void setActionsAndPrice(List<Models_layout_data_client_list> actionsAndPrice) {
        this.actionsAndPrice = actionsAndPrice;
    }

}
