package com.apps.vladan.weather_widget;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import static com.apps.vladan.weather_widget.UpdateWidgetService.iconList;
import static com.apps.vladan.weather_widget.UpdateWidgetService.maxTempList;
import static com.apps.vladan.weather_widget.UpdateWidgetService.minTempList;
import static com.apps.vladan.weather_widget.UpdateWidgetService.timeList;

/**
 * Created on 4/28/2017.
 */

public class DownLoadForecastData extends AsyncTask<String,String,String> {
    long rowOffset;
    Calendar myCalendar;

    DownLoadForecastData(long rowOffset){

        this.rowOffset=rowOffset;
    }

    @Override
    protected void onPreExecute() {
       myCalendar=Calendar.getInstance();


        while (timeList.size() != 0) {
        timeList.clear();}

        while (iconList.size() != 0) {
           iconList.clear();}

        while (maxTempList.size() != 0) {
            maxTempList.clear();}

        while (minTempList.size() != 0) {
            minTempList.clear();}



    }



    @Override
    protected String doInBackground(String... strings) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                Log.v("Weather",buffer.toString());
            }
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
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
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        try {
            JSONObject forecastFive = new JSONObject(response);
            JSONArray forecastList = forecastFive.getJSONArray("list");
            for (int i = 0; i < 5; i++) {
                JSONObject threeHour = forecastList.getJSONObject(i);
                String time = threeHour.getString("dt");
                myCalendar.setTimeInMillis(((Long.parseLong(time) + (rowOffset)) * 1000));
                int myHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                timeList.add(i, myHour);
                JSONObject main = threeHour.getJSONObject("main");
                Double tempMin = main.getDouble("temp_min");
                minTempList.add(i, String.valueOf(Math.round(tempMin)));
                Double tempMax = main.getDouble("temp_max");
                maxTempList.add(i, String.valueOf(Math.round(tempMax)));
                JSONArray weatherArray = threeHour.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String icon = weather.getString("icon");
                iconList.add(i, icon);

            }


            Log.d("Response", String.valueOf(forecastList));
            Log.v("timeList", timeList.toString());

        } catch (JSONException e) {
            e.printStackTrace();

        }
        UpdateWidgetService.flag1=true;



    }
}
