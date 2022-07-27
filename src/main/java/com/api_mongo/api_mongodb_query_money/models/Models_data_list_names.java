package com.api_mongo.api_mongodb_query_money.models;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ListActions")
public class Models_data_list_names {

    @Id
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID id;

    private String nameAction;

    public Models_data_list_names(UUID id, String nameAction) {
        this.id = id;
        this.nameAction = nameAction;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNameAction() {
        return nameAction;
    }

    public void setNameAction(String nameAction) {
        this.nameAction = nameAction;
    }

}
