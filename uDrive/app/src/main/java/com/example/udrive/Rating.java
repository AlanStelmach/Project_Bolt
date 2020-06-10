package com.example.udrive;

public class Rating {
    private float stars;
    private String text;
    private String name_surname;

    public Rating()
    {

    }

    public Rating(float stars, String text, String name_surname) {
        this.stars = stars;
        this.text = text;
        this.name_surname = name_surname;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName_surname() {
        return name_surname;
    }

    public void setName_surname(String name_surname) {
        this.name_surname = name_surname;
    }
}
