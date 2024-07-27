package com.abhi.weatherapp;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}
