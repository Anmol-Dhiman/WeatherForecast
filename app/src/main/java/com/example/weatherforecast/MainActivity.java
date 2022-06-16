package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.example.weatherforecast.Adapter.WeatherAdapter;
import com.example.weatherforecast.Repository.WeatherModel;
import com.example.weatherforecast.ViewModel.RetrofitViewModel;
import com.example.weatherforecast.databinding.ActivityMainBinding;


import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    private static final int PERMISSION_CODE = 200;
    private ActivityMainBinding binding;
    private LocationManager locationManager;
    private List<WeatherModel> weatherModelList;
    private Location location;
    private WeatherAdapter adapter;
    private RetrofitViewModel viewModel;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


//        setting up the recycler view
        adapter = new WeatherAdapter(MainActivity.this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkPermission()) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            String cityName = getCityName(location.getLatitude(), location.getLongitude());
            apiCall("Jind");


        } else {
            requestPermission();
        }

        viewModel = new ViewModelProvider(this).get(RetrofitViewModel.class);
        viewModel.getWeatherList().observe(this, new Observer<List<WeatherModel>>() {
            @Override
            public void onChanged(List<WeatherModel> list) {
                if (list != null) {
                    weatherModelList = list;
                    adapter.setMovieList(list);

                } else {
                    Toast.makeText(MainActivity.this, "There is some issue with network....", Toast.LENGTH_LONG).show();
                }
            }
        });


        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.userInput.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter the city name.", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(binding.userInput.getApplicationWindowToken(), 0);
                    apiCall(binding.userInput.getText().toString().trim());
                }
            }
        });

    }

    private void apiCall(String cityName) {
        viewModel.makeApiCall(cityName);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
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