package com.abhi.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView cityCountryTextView, coordinatesTextView, weatherTextView, temperatureTextView, pressureTextView, humidityTextView, windTextView, cloudinessTextView;
    private EditText cityEditText;
    private Button fetchWeatherButton;
    private CardView weatherCard;
    private static final String API_KEY = "da6f40a6f9e5d3895d3be31f8dab6dfb"; // Replace with your API key
//    private static final String CITY_NAME = "London";
//    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=" + CITY_NAME + "&appid=" + API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityCountryTextView = findViewById(R.id.cityCountryTextView);
        coordinatesTextView = findViewById(R.id.coordinatesTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        windTextView = findViewById(R.id.windTextView);
        cloudinessTextView = findViewById(R.id.cloudinessTextView);
        cityEditText = findViewById(R.id.cityEditText);
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton);
        weatherCard = findViewById(R.id.weatherCard);

        fetchWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityEditText.getText().toString().trim();
                if (!cityName.isEmpty()) {
                    String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=metric";
                    // Perform the network request on a separate thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            fetchWeatherData(apiUrl);
                        }
                    }).start();
                } else {
                    weatherCard.setVisibility(View.GONE);
                }
            }
        });

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchWeatherButton.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //weatherTextView.setText(API_URL);
//        Toast.makeText(this, "API_URL:"+API_URL, Toast.LENGTH_SHORT).show();

    }

    private void fetchWeatherData(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();
                String response = responseBuilder.toString();
                Log.d("WeatherApp", "API Response: " + response);
                updateUI(response);
            } else {
                Log.e("WeatherApp", "Error response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("WeatherApp", "Exception: " + e.getMessage());
        }
    }

    private void updateUI(final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    // Get coordinates
                    JSONObject coord = jsonObject.getJSONObject("coord");
                    double lon = coord.getDouble("lon");
                    double lat = coord.getDouble("lat");

                    // Get weather information
                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String weatherMain = weather.getString("main");
                    String weatherDescription = weather.getString("description");

                    // Get main information
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    double feelsLike = main.getDouble("feels_like");
                    double tempMin = main.getDouble("temp_min");
                    double tempMax = main.getDouble("temp_max");
                    int pressure = main.getInt("pressure");
                    int humidity = main.getInt("humidity");

                    // Get wind information
                    JSONObject wind = jsonObject.getJSONObject("wind");
                    double windSpeed = wind.getDouble("speed");
                    int windDeg = wind.getInt("deg");

                    // Get cloud information
                    JSONObject clouds = jsonObject.getJSONObject("clouds");
                    int cloudiness = clouds.getInt("all");

                    // Get other information
                    String cityName = jsonObject.getString("name");
                    String country = jsonObject.getJSONObject("sys").getString("country");

                    cityCountryTextView.setText("City: " + cityName + ", " + country);
                    coordinatesTextView.setText("Coordinates: (" + lat + ", " + lon + ")");
                    weatherTextView.setText("Weather: " + weatherMain + " (" + weatherDescription + ")");
                    temperatureTextView.setText("Temperature: " + temperature + "°C (Feels like: " + feelsLike + "°C)\nMin Temp: " + tempMin + "°C, Max Temp: " + tempMax + "°C");
                    pressureTextView.setText("Pressure: " + pressure + " hPa");
                    humidityTextView.setText("Humidity: " + humidity + "%");
                    windTextView.setText("Wind: " + windSpeed + " m/s at " + windDeg + "°");
                    cloudinessTextView.setText("Cloudiness: " + cloudiness + "%");

                    weatherCard.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    weatherCard.setVisibility(View.GONE);
                }
            }
        });
    }
}