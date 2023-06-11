package com.example.finalproj;

import java.io.Serializable;

public class Transaction implements Serializable {
    String name, time, type;
    double price, amount;

    public Transaction(String name, String time, String type, double price, double amount) {
        this.name = name;
        this.time = time;
        this.type = type;
        this.price = price;
        this.amount = amount;
    }

    public String getName() { 
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }
}
