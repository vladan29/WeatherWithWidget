package com.apps.vladan.weather_widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created  on 4/28/2017.
 */

public class GetOffset extends AsyncTask<String, String, String> {

    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String unit = "°C";
    String urlUnit = "metric";
    String city;
    String forecastUrl;
    long rawOffsetL;
    String timeZone;


    GetOffset(Context context) {
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        prefs = this.context.getSharedPreferences("com.apps.vladan.weather_widget", Context.MODE_PRIVATE);
        editor = prefs.edit();

        unit = prefs.getString("Unit", "°C");
        switch (unit) {
            case "°C":
                urlUnit = "metric";
                break;
            case "F":
                urlUnit = "imperial";
                break;
        }

        city = prefs.getString("Place", "Smederevo");
        String urlCity = city.replace(" ", "%20");
        forecastUrl = "http://api.openweathermap.org/data/2.5/forecast?mode=json&units=" + urlUnit +
                "&q=" + urlCity + "&cnt=0&appid=9f37a5607aeaae52639115165e108ea6";
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);

            }
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {

        } finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject placeObject = new JSONObject(s);
            String rawOffset = placeObject.getString("rawOffset");
            rawOffsetL = Long.parseLong(rawOffset);
            timeZone = placeObject.getString("timeZoneId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new DownLoadForecastData(rawOffsetL).execute(forecastUrl);

    }
}
