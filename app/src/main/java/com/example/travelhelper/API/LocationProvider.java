package com.example.travelhelper.API;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationProvider {

    public interface CityLoaded {
        void CityLoaded(boolean isLoaded, List<Location> locationList);
    }

    public static void fetchCityData(final CityLoaded cityLoaded, double latitude, double longitude) {

        String language;

        //String language = Locale.getDefault().getLanguage();

        if (Locale.getDefault().getDisplayLanguage().equals("English")) language = "en";
        else if (Locale.getDefault().getDisplayLanguage().equals("русский")) language = "ru";
        else if (Locale.getDefault().getDisplayLanguage().equals("polski")) language = "pl";
        else language = "en";

        double roundLongitude = Math.round(longitude * 1000.0) / 1000.0;
        double roundLatitude = Math.round(latitude * 1000.0) / 1000.0;

        LocationService locationService = RetrofitInstance.getRetrofitInstance().create(LocationService.class);

        Call<Location[]> cityApiCall = locationService.findCity("CITY", roundLongitude, language, roundLatitude, 1, 5);

        cityApiCall.enqueue(new Callback<Location[]>() {

            @Override
            public void onResponse(Call<Location[]> call, Response<Location[]> response) {
                cityLoaded.CityLoaded(true, Arrays.asList(response.body()));
            }

            @Override
            public void onFailure(Call<Location[]> call, Throwable t) {
                cityLoaded.CityLoaded(false, null);
            }
        });
    }
}
