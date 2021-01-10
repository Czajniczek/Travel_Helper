package com.example.travelhelper.API;

import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("localizedName")
    private String name;

    @SerializedName("population")
    private String population;

    @SerializedName("country")
    private Country country;

    @SerializedName("adminDivision1")
    private Province province;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) { this.province = province; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }
}
