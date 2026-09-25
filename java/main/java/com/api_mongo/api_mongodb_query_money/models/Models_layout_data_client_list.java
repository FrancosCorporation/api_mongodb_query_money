package com.api_mongo.api_mongodb_query_money.models;

import javax.validation.constraints.*;

public class Models_layout_data_client_list {

    private String nameAction;
    private String dateBuy;
    private String priceBuy;
    private String quantity;

    public String getNameAction() {
        return nameAction;
    }

    public void setNameAction(String nameAction) {
        this.nameAction = nameAction;
    }

    public String getDateBuy() {
        return dateBuy;
    }

    public void setDateBuy(String dateBuy) {
        this.dateBuy = dateBuy;
    }

    public String getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(String priceBuy) {
        this.priceBuy = priceBuy;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
