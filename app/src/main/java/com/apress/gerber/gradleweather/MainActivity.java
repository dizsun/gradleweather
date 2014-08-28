package com.apress.gerber.gradleweather;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends ListActivity implements Runnable{

    private Handler handler;
    private TemperatureAdapter temperatureAdapter;
    private TemperatureData temperatureData;
    private Dialog splashDialog;
    String [] weekdays = {
            "Sunday","Monday","Tuesday",
            "Wednesday","Thursday","Friday",
            "Saturday"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperatureAdapter = new TemperatureAdapter(this);
        setListAdapter(temperatureAdapter);
        showSplashScreen();
        handler = new Handler();
        AsyncTask.execute(this);
    }

    private void showSplashScreen() {
        splashDialog = new Dialog(this, R.style.splash_screen);
        splashDialog.setContentView(R.layout.activity_splash);
        splashDialog.setCancelable(false);
        splashDialog.show();
    }

    private void onDataLoaded() {
        ((TextView) findViewById(R.id.currentDayOfWeek)).setText(weekdays[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+1]);
        ((TextView) findViewById(R.id.currentTemperature)).setText(temperatureData.getCurrentConditions().get(TemperatureData.CURRENT));
        ((TextView) findViewById(R.id.currentDewPoint)).setText(temperatureData.getCurrentConditions().get(TemperatureData.DEW_POINT));
        ((TextView) findViewById(R.id.currentHigh)).setText(temperatureData.getCurrentConditions().get(TemperatureData.HIGH));
        ((TextView) findViewById(R.id.currentLow)).setText(temperatureData.getCurrentConditions().get(TemperatureData.LOW));
        if (splashDialog!=null) {
            splashDialog.dismiss();
            splashDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        temperatureData = new TemperatureData(this);
        temperatureAdapter.setTemperatureData(temperatureData);
        // Set Runnable to remove splash screen just in case
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onDataLoaded();
            }
        }, 5000);
    }
}
