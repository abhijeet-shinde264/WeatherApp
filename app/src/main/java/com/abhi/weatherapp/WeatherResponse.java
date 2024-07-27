package com.abhi.weatherapp;

public class WeatherResponse {
    private Main main;
    private String name;

    public Main getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    public class Main {
        private double temp;

        public double getTemp() {
            return temp;
        }
    }
}
