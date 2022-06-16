package com.example.weatherforecast.Repository.Retrofit;

import com.example.weatherforecast.Repository.WeatherModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

import retrofit2.http.QueryMap;

public interface APIService {

    //    base url : - https://api.weatherapi.com/
    @GET("v1/forecast.json")
    Call<List<WeatherModel>> get(@QueryMap Map<String, String> param);
}
