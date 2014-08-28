package com.apress.gerber.gradleweather;

import android.graphics.drawable.Drawable;

/**
* Created by Clifton
* Copyright 8/27/2014.
*/
class TemperatureItem {

    private final Drawable image;
    private final String day;
    private final String forecast;
    private final String description;

    public TemperatureItem(Drawable image, String day, String forecast, String description) {
        this.image = image;
        this.day = day;
        this.forecast = forecast;
        this.description = description;
    }

    public String getDay() {
        return day;
    }

    public String getForecast() {
        return forecast;
    }

    public String getDescription() {
        return description;
    }

    public Drawable getImageDrawable() {
        return image;
    }
}
