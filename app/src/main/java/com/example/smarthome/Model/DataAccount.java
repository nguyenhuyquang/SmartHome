package com.example.smarthome.Model;

public class DataAccount {
    String phone;
    String name;

    public DataAccount(String name, String phone) {

        this.phone = phone;
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public DataAccount() {
    }
}
