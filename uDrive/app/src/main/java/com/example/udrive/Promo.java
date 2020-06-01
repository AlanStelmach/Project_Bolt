package com.example.udrive;

public class Promo {
    private String promo;
    private String value;

    public Promo(String promo, String value) {
        this.promo = promo;
        this.value = value;
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
