package com.api_mongo.api_mongodb_query_money.models;

import javax.validation.constraints.*;

public class Models_layout_data_client_list {

    @NotBlank
    @NotNull
    private String nameAction;
    @NotBlank
    @NotNull
    private String dateBuy;
    @NotBlank
    @NotNull
    private String priceBuy;
    @NotBlank
    @NotNull
    private String quantity;

    
    public Models_layout_data_client_list(String nameAction, String dateBuy, String priceBuy, String quantity) {
        this.nameAction = nameAction;
        this.dateBuy = dateBuy;
        this.priceBuy = priceBuy;
        this.quantity = quantity;
    }
    public Models_layout_data_client_list(String[] listData) {
        this.nameAction = listData[0];
        this.dateBuy = listData[1];
        this.priceBuy = listData[2];
        this.quantity = listData[3]; 
    }
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
