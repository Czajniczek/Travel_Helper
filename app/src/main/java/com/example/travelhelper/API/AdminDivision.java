package com.example.travelhelper.API;

import com.google.gson.annotations.SerializedName;

public class AdminDivision {

    @SerializedName("localizedName")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
