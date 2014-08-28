package com.apress.gerber.gradleweather;

import com.apress.gerber.weather.request.NationalWeatherRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Clifton
 * Copyright 8/28/2014.
 */
public class NationalWeatherRequestData implements TemperatureData {
    public NationalWeatherRequestData() {
        new NationalWeatherRequest();
    }

    @Override
    public List<TemperatureItem> getTemperatureItems() {
        return new ArrayList<TemperatureItem>();
    }

    @Override
    public Map<String, String> getCurrentConditions() {
        return new HashMap<String, String>();
    }
}
