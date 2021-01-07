package com.example.travelhelper.API;

import com.google.gson.annotations.SerializedName;

public class Province {

    @SerializedName("localizedName")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
