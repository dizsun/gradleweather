package com.apress.gerber.gradleweather;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

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

    public static final double DEFAULT_LATITUDE = 37.368830;
    public static final double DEFAULT_LONGITUDE = -122.036350;
    private LocationManager locationManager;
    private String city = "?";

    public NationalWeatherRequestData(Context context) {
        Location location = getLocation(context);
        new NationalWeatherRequest(location);
    }

    @Override
    public List<TemperatureItem> getTemperatureItems() {
        return new ArrayList<TemperatureItem>();
    }

    @Override
    public Map<String, String> getCurrentConditions() {
        return new HashMap<String, String>();
    }

    @Override
    public CharSequence getCity() {
        return city;
    }

    private Location getLocation(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            return location;
        } else {
            Location defaultLocation = new Location(provider);
            defaultLocation.setLatitude(DEFAULT_LATITUDE);
            defaultLocation.setLongitude(DEFAULT_LONGITUDE);
            return defaultLocation;
        }
    }
}
