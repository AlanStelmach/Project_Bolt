package com.example.udrive;

public class User {

    private String name;
    private String surname;
    private String email;
    private String pnumber;
    private String wallet;
    private String isdriver;

    public User()
    {

    }

    public User(String name, String surname, String email, String pnumber,String wallet, String isdriver)
    {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.pnumber = pnumber;
        this.wallet = wallet;
        this.isdriver = isdriver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getWallet() { return wallet; }

    public void setWallet(String wallet) { this.wallet = wallet; }

    public String getIsdriver() { return isdriver; }

    public void setIsdriver(String isdriver) { this.isdriver = isdriver; }
}
