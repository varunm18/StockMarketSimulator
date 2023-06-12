package com.example.finalproj;

import android.widget.TextView;

import org.json.JSONObject;

public class AsyncParams{
    JSONObject json;
    int action;
    String name;
    TextView text, total;
    double shares;

    public AsyncParams(JSONObject json, int action, String name, TextView text, TextView total, double shares) {
        this.json = json;
        this.action = action;
        this.name = name;
        this.text = text;
        this.total = total;
        this.shares = shares;
    }

    public AsyncParams(JSONObject json, int action, String name, TextView text) {
        this.json = json;
        this.action = action;
        this.name = name;
        this.text = text;
        total = null;
        shares = 0;
    }

    public JSONObject getJson() {
        return json;
    }

    public int getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public TextView getText() {
        return text;
    }

    public TextView getTotal() {
        return total;
    }

    public double getShares() {
        return shares;
    }
}
