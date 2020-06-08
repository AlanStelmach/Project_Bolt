package com.example.udrive;

public class HistoryItem {
    private String current_location;
    private String destiny_location;
    private String price;
    private String status;

    public  HistoryItem()
    {

    }

    public HistoryItem(String current_location, String destiny_location, String price, String status) {
        this.current_location = current_location;
        this.destiny_location = destiny_location;
        this.price = price;
        this.status = status;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public String getDestiny_location() {
        return destiny_location;
    }

    public void setDestiny_location(String destiny_location) {
        this.destiny_location = destiny_location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}