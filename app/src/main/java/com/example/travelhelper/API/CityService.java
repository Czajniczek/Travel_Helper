package com.example.travelhelper.API;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CityService {
    @GET("places?rapidapi-key=0a23eb31bbmshdb9f7644f4389efp13c313jsne5092afa3c11")
    Call<City[]> findCity(@Query("type") String city, @Query("longitude") double longitude, @Query("language") String language, @Query("latitude") double latitude, @Query("limit") double limit, @Query("accuracyRadiusKm") double km);
}