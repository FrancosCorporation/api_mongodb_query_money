package com.api_mongo.api_mongodb_query_money.models;

public class Models_data_b3 extends Models_data_b3_names{

    private String Date;
    private String Open;
    private String High;
    private String Low;
    private String Close;
    private String Price;
    private String Volume;

    public Models_data_b3(String[] listData) {
        Date = listData[0];
        Open = listData[1];
        High = listData[2];
        Low = listData[3];
        Close = listData[4];
        Price = listData[5];
        Volume = listData[6];
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getOpen() {
        return Open;
    }

    public void setOpen(String open) {
        Open = open;
    }

    public String getHigh() {
        return High;
    }

    public void setHigh(String high) {
        High = high;
    }

    public String getLow() {
        return Low;
    }

    public void setLow(String low) {
        Low = low;
    }

    public String getClose() {
        return Close;
    }

    public void setClose(String close) {
        Close = close;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String volume) {
        Volume = volume;
    }

}
