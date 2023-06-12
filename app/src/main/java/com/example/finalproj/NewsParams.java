package com.example.finalproj;

import org.json.JSONArray;

public class NewsParams
{
    String name;
    JSONArray json;

    public NewsParams(String name, JSONArray json) {
        this.name = name;
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public JSONArray getJson() {
        return json;
    }
}
