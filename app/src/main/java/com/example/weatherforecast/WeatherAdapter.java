package com.example.weatherforecast;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherModle> weatherDataArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModle> weatherDataArrayList) {
        this.context = context;
        this.weatherDataArrayList = weatherDataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherModle weather = weatherDataArrayList.get(position);
        holder.temperature.setText(weather.getTemperature() + "°c");
        holder.windSpeed.setText(weather.getWindSpeed() + " Km/h");

//        changing the image w.r.t to weather

        Picasso.get().load("https:" + weather.getIcon()).into(holder.conditionImage);

//        changing the date into simple format

        SimpleDateFormat input = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(weather.getTime());

            holder.time.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time, temperature, windSpeed;
        private ImageView conditionImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            temperature = itemView.findViewById(R.id.temperatureRecyclerView);
            windSpeed = itemView.findViewById(R.id.windSpeedRecyclerView);
            conditionImage = itemView.findViewById(R.id.conditionImageRecyclerView);
        }
    }
}
