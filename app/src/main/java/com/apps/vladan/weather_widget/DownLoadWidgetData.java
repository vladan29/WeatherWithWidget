package com.apps.vladan.weather_widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

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

/**
 * Created  on 3/9/2017.
 */

class DownLoadWidgetData extends AsyncTask<String, String, String> {
    private String icon;
    private Double temp;
    private Context context;
    private String mainDescription;
    private String description;


    DownLoadWidgetData(Context context) {
        this.context = context;
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject weatherData = new JSONObject(jsonObject.getString("main"));
            temp = Double.parseDouble(weatherData.getString("temp"));
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            icon = weatherObject.getString("icon");
            mainDescription=weatherObject.getString("main");
            description=weatherObject.getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        SharedPreferences prefs = this.context.getSharedPreferences("com.apps.vladan.weather_widget", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("vTemperature", String.valueOf(Math.round(temp)));
        editor.putString("image", icon);
        editor.putString("mainDescription",mainDescription);
        editor.putString("description",description);
        editor.apply();
        UpdateWidgetService.flag = true;

    }
}
