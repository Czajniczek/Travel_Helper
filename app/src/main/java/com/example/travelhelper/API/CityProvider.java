package com.example.travelhelper.API;

import com.example.travelhelper.Dialogues.LoadingDialog;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityProvider {

    public interface CityLoaded {
        void CityLoaded(boolean isLoaded, List<City> cityList);
    }

    public static void fetchCityData(final CityLoaded cityLoaded, double latitude, double longitude) {

        String language = Locale.getDefault().getLanguage();

        /*if (Locale.getDefault().getDisplayLanguage().equals("English")) language = "en";
        else if (Locale.getDefault().getDisplayLanguage().equals("русский")) language = "ru";
        else if (Locale.getDefault().getDisplayLanguage().equals("polski")) language = "pl";
        else language = "en";*/

        double roundLongitude = Math.round(longitude * 1000.0) / 1000.0;
        double roundLatitude = Math.round(latitude * 1000.0) / 1000.0;

        CityService cityService = RetrofitInstance.getRetrofitInstance().create(CityService.class);

        Call<City[]> cityApiCall = cityService.findCity("CITY", roundLongitude, language, roundLatitude, 1, 5);

        cityApiCall.enqueue(new Callback<City[]>() {
            @Override
            public void onResponse(Call<City[]> call, Response<City[]> response) {
                cityLoaded.CityLoaded(true, Arrays.asList(response.body()));
            }

            @Override
            public void onFailure(Call<City[]> call, Throwable t) {
                cityLoaded.CityLoaded(false, null);
            }
        });
    }
}
