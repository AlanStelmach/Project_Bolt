package com.example.udrive;

public class CreditCard {
    private String name;
    private String number;
    private String date;
    private String cvv;

    public CreditCard()
    {

    }

    public CreditCard(String name, String number, String date, String cvv) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.cvv = cvv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
