package com.apress.gerber.gradleweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clifton
 * Copyright 8/27/2014.
 */
public class TemperatureAdapter extends BaseAdapter {
    private final Context context;
    List<TemperatureItem>items;

    public TemperatureAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<TemperatureItem>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : createView(parent);
        TemperatureItem temperatureItem = items.get(position);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageDrawable(temperatureItem.getImageDrawable());
        ((TextView) view.findViewById(R.id.dayTextView)).setText(temperatureItem.getDay());
        ((TextView) view.findViewById(R.id.briefForecast)).setText(temperatureItem.getForecast());
        ((TextView) view.findViewById(R.id.description)).setText(temperatureItem.getDescription());
        return view;
    }

    private View createView(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.temperature_summary, parent, false);
    }

    public void setTemperatureData(TemperatureData temperatureData) {
        items = temperatureData.getTemperatureItems();
        notifyDataSetChanged();
    }
}
