package com.example.udrive;

public class Promo {
    private String promo_name;
    private Integer value;

    public Promo()
    {

    }

    public Promo(String promo_name, Integer value) {
        this.promo_name = promo_name;
        this.value = value;
    }

    public String getPromo_name() {
        return promo_name;
    }

    public void setPromo_name(String promo_name) {
        this.promo_name = promo_name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}
