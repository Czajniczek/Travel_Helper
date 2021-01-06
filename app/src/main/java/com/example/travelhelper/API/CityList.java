package com.example.travelhelper.API;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CityList {


    @SerializedName("")

    private List<City> cityList;

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
