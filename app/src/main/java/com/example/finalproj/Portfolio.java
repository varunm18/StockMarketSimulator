package com.example.finalproj;

public class Portfolio {
    String name;
    double amount;

    public Portfolio() {
    }

    public Portfolio(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void addAmount(double amount) {
        this.amount+=amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
