package com.example.weatherforecast.Repository;

public class WeatherModel {

    String time;
    String temp_c;
    String icon;
    String wind_kph;

    public WeatherModel(String time, String temperature, String icon, String windSpeed) {
        this.time = time;
        this.temp_c = temperature;
        this.icon = icon;
        this.wind_kph = windSpeed;
    }

    public String getTime() {
        return time;
    }



    public String getTemperature() {
        return temp_c;
    }


    public String getIcon() {
        return icon;
    }


    public String getWindSpeed() {
        return wind_kph;
    }

}
