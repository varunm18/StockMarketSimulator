package com.example.finalproj;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String username, password;
    double money;
    ArrayList<Transaction> transactions;

    public User() {

    }

    public User(String username, String password, double money, ArrayList<Transaction> transactions) {
        this.username = username;
        this.password = password;
        this.money = money;
        this.transactions = transactions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void purchase(double money)
    {
        this.money-=money;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransactions(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
