package com.api_mongo.api_mongodb_query_money.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "NamesActions")
public class Models_data_b3_names {

    @Id
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID id;

    private String nameAction;

    private LocalDateTime dataCreate;

    private LocalDateTime changeDate;

    private List<Models_data_b3> dateandprices;

    public Models_data_b3_names(UUID id, String nameAction, LocalDateTime dataCreate, LocalDateTime changeDate,
            List<Models_data_b3> dateandprices) {
        this.id = id;
        this.nameAction = nameAction;
        this.dataCreate = dataCreate;
        this.changeDate = changeDate;
        this.dateandprices = dateandprices;
    }

    public Models_data_b3_names() {

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

    public List<Models_data_b3> getDateandprices() {
        return dateandprices;
    }

    public void setDateandprices(List<Models_data_b3> dateandprices) {
        this.dateandprices = dateandprices;
    }

}
