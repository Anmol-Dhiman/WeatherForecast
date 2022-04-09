package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherforecast.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    private static final int PERMISSION_CODE = 200;
    private ActivityMainBinding binding;
    private LocationManager locationManager;
    private ArrayList<WeatherModle> weatherModles = new ArrayList<>();
    private WeatherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        if (checkPermission()) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            getJson(getCityName(location.getLatitude(), location.getLongitude()));

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJson(binding.userInput.getText().toString().trim());
            }
        });

    }


    private void getJson(String cityName) {
        adapter = new WeatherAdapter(MainActivity.this, weatherModles);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String url = "https://api.weatherapi.com/v1/forecast.json?key=cf08f776e9d64f458ba151049222502&q=" + cityName + "&days=1&aqi=no&alerts=no";
        binding.cityName.setText(cityName);
        binding.userInput.setText("");
        Log.d("getJson", "" + url);


        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("try", "here in the try block");
                    int temp_cel = response.getJSONObject("current").getInt("temp_c");
                    binding.temperature.setText(temp_cel + "°c");

                    String conditionText = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    binding.condition.setText(conditionText);

                    String conditionImage = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:" + conditionImage).into(binding.conditionIcon);

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject forecastday = forecast.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hour = forecastday.getJSONArray(("hour"));
                    weatherModles.clear();
                    for (int i = 0; i < hour.length(); i++) {
                        Log.d("forLoop", "here we are in the for loop");
                        JSONObject slot = hour.getJSONObject(i);

                        String time = slot.getString("time");
                        Double temper = slot.getDouble("temp_c");
                        String img = slot.getJSONObject("condition").getString("icon");
                        Double wind = slot.getDouble("wind_kph");

                        weatherModles.add(new WeatherModle(time, "" + temper, img, "" + wind));
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "There is not any data for the city named " + cityName, Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);


    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private String getCityName(double latitude, double longitude) {
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();

                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("cityName", cityName);
        return cityName;

    }


}