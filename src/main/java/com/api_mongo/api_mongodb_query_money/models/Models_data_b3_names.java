package com.api_mongo.api_mongodb_query_money.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;

public class Models_data_b3_names {
    
    @Id
    private String nameAction;

    private LocalDateTime dataCreate;

    private LocalDateTime changeDate;

    private ArrayList<Models_data_b3> dateandprices ;

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

    public ArrayList<Models_data_b3> getDateandprices() {
        return dateandprices;
    }

    public void setDateandprices(ArrayList<Models_data_b3> dateandprices) {
        this.dateandprices = dateandprices;
    }


    

    
    

}
