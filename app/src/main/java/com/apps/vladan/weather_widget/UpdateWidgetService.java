package com.apps.vladan.weather_widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends IntentService {


    Bitmap image;
    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    static Boolean flag = false;
    public static Boolean flag1 = false;
    Timer timer;
    String unit = "°C";
    String urlUnit = "metric";
    String city;
    String description;
    SharedPreferences prefs;
    String requestUrl;
    String forecastUrl;
    LinearLayout widget;
    private static final String TAG = "com.apps.vladan.weather_widget";
    public static final String MORE = "more";
    public static final String LESS = "less";
    String MORE_LESS;
    Intent startIntent;
    int state = 0;
    String moreLessText;
    static List<Integer> timeList = new ArrayList<>();
    static List<String> minTempList = new ArrayList<>();
    static List<String> maxTempList = new ArrayList<>();
    static List<String> iconList = new ArrayList<>();
    DateFormat myFormat;
    Calendar myCalendar;
    long currentTime;
    String offsetUrl;
    String placeCord;



    public UpdateWidgetService() {
        super("UpdateWidgetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        while(timeList.size()<5){
            timeList.add(0);
        }
        while(minTempList.size()<5){
            minTempList.add("");
        }
        while(maxTempList.size()<5){
            maxTempList.add("");
        }
        while(timeList.size()<5){
            timeList.add(0);
        }
        while(iconList.size()<5){
            iconList.add("10d");
        }


        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        widget = (LinearLayout) inflater.inflate(R.layout.app_widget, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            widget.setClipToOutline(true);
        }
        currentTime=System.currentTimeMillis()/1000;
        myCalendar = Calendar.getInstance();
        myFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        startIntent = new Intent(getApplicationContext(), AppWidget.class);
        remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),
                R.layout.app_widget);

        prefs = getApplicationContext().getSharedPreferences
                ("com.apps.vladan.weather_widget", Context.MODE_PRIVATE);
        state = prefs.getInt("state", 0);


        moreLessText = prefs.getString("more_less", "Show more");
        if (moreLessText.equals("Show more")) {
            MORE_LESS = MORE;
        } else {
            MORE_LESS = LESS;
        }


    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        unit = prefs.getString("Unit", "°C");
        switch (unit) {
            case "°C":
                urlUnit = "metric";
                break;
            case "F":
                urlUnit = "imperial";
                break;
        }


        city = prefs.getString("Place", "New York");
        placeCord=prefs.getString("placeCord","40.712783699999996,-74.0059413");



        String urlCity = city.replace(" ", "%20");
        requestUrl = "http://api.openweathermap.org/data/2.5/weather?mode=json&units=" + urlUnit +
                "&q=" + urlCity + "&cnt=0&appid=9f37a5607aeaae52639115165e108ea6";
        forecastUrl = "http://api.openweathermap.org/data/2.5/forecast?mode=json&units=" + urlUnit +
                "&q=" + urlCity + "&cnt=5&appid=9f37a5607aeaae52639115165e108ea6";
        offsetUrl="https://maps.googleapis.com/maps/api/timezone/json?location=" + placeCord +
                "&timestamp=" + String.valueOf(currentTime) + "&sensor=false";


        appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        for (final int widgetId : allWidgetIds) {


            if (state==0){new DownLoadWidgetData(this.getApplicationContext()).execute(requestUrl);
                flag1=true;
            }else {
            new GetOffset(this.getApplicationContext()).execute(offsetUrl);
            new DownLoadWidgetData(this.getApplicationContext()).execute(requestUrl);
            }


            remoteViews.setOnClickPendingIntent(R.id.more_less, getSpecificPendingIntent(getApplicationContext(), MORE_LESS));

            Intent pickPlace = new Intent(this, PickCityActivity.class);
            PendingIntent startPickPlace = PendingIntent.getActivity(this, 1, pickPlace, PendingIntent.FLAG_CANCEL_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.city, startPickPlace);


            Intent pickUnit = new Intent(this, PickUnitActivity.class);
            PendingIntent startPickUnit = PendingIntent.getActivity(this, 1, pickUnit, PendingIntent.FLAG_CANCEL_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.temperature, startPickUnit);


            Intent clickIntent = new Intent(this.getApplicationContext(), AppWidget.class);
            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
            PendingIntent updatePendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.weather_icon, updatePendingIntent);


            timer = new Timer();
            TimerTask updateWidget = new TimerTask() {

                @Override
                public void run() {
                    if (flag&&flag1) {
                        flag = false;
                        flag1=false;


                        try {
                            image = BitmapFactory.decodeStream((InputStream) new URL("http://openweathermap.org/img/w/"
                                    + prefs.getString("image", "") + ".png").getContent());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        if (state == 0) {

                            remoteViews.setTextViewText(R.id.more_less, "Show more");
                            remoteViews.setViewVisibility(R.id.weather_icon1, View.GONE);
                            remoteViews.setTextViewText(R.id.temperature, prefs.getString("vTemperature", "") + "" + unit);
                            remoteViews.setImageViewBitmap(R.id.weather_icon, image);
                            if (city.length() > 12) {
                                remoteViews.setTextViewTextSize(R.id.city, TypedValue.COMPLEX_UNIT_PX, 50f);
                            } else {
                                remoteViews.setTextViewTextSize(R.id.city, TypedValue.COMPLEX_UNIT_PX, 80f);
                            }
                            remoteViews.setTextViewText(R.id.city, city);
                            remoteViews.setTextViewText(R.id.mainDescription, prefs.getString("mainDescription", ""));
                            description = prefs.getString("description", "");
                            if (description.length() > 1) {
                                description = description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase();
                            }
                            remoteViews.setTextViewText(R.id.description, description);

                        } else {

                            remoteViews.setTextViewText(R.id.more_less, "Show less");
                            remoteViews.setViewVisibility(R.id.weather_icon1, View.VISIBLE);


                            remoteViews.setTextViewText(R.id.time_1, String.valueOf(timeList.get(0)));
                            remoteViews.setTextViewText(R.id.time_2, String.valueOf(timeList.get(1)));
                            remoteViews.setTextViewText(R.id.time_3, String.valueOf(timeList.get(2)));
                            remoteViews.setTextViewText(R.id.time_4, String.valueOf(timeList.get(3)));
                            remoteViews.setTextViewText(R.id.time_5, String.valueOf(timeList.get(4)));

                            remoteViews.setTextViewText(R.id.temperature_1, minTempList.get(0) + "" + unit + "\\" + maxTempList.get(0) + "" + unit);
                            remoteViews.setTextViewText(R.id.temperature_2, minTempList.get(1) + "" + unit + "\\" + maxTempList.get(1) + "" + unit);
                            remoteViews.setTextViewText(R.id.temperature_3, minTempList.get(2) + "" + unit + "\\" + maxTempList.get(2) + "" + unit);
                            remoteViews.setTextViewText(R.id.temperature_4, minTempList.get(3) + "" + unit + "\\" + maxTempList.get(3) + "" + unit);
                            remoteViews.setTextViewText(R.id.temperature_5, minTempList.get(4) + "" + unit + "\\" + maxTempList.get(4) + "" + unit);


                            remoteViews.setImageViewBitmap(R.id.icon_1, decodeBitmap(iconList.get(0)));
                            remoteViews.setImageViewBitmap(R.id.icon_2, decodeBitmap(iconList.get(1)));
                            remoteViews.setImageViewBitmap(R.id.icon_3, decodeBitmap(iconList.get(2)));
                            remoteViews.setImageViewBitmap(R.id.icon_4, decodeBitmap(iconList.get(3)));
                            remoteViews.setImageViewBitmap(R.id.icon_5, decodeBitmap(iconList.get(4)));

                            remoteViews.setTextViewText(R.id.temperature, prefs.getString("vTemperature", "") + "" + unit);
                            remoteViews.setImageViewBitmap(R.id.weather_icon, image);
                            if (city.length() > 12) {
                                remoteViews.setTextViewTextSize(R.id.city, TypedValue.COMPLEX_UNIT_PX, 50f);
                            } else {
                                remoteViews.setTextViewTextSize(R.id.city, TypedValue.COMPLEX_UNIT_PX, 80f);
                            }
                            remoteViews.setTextViewText(R.id.city, city);
                            remoteViews.setTextViewText(R.id.mainDescription, prefs.getString("mainDescription", ""));
                            description = prefs.getString("description", "");
                            if (description.length() > 1) {
                                description = description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase();
                            }
                            remoteViews.setTextViewText(R.id.description, description);
                        }



                        appWidgetManager.updateAppWidget(widgetId, remoteViews);
                        if (timer != null) {
                            timer.cancel();
                            timer.purge();
                            timer = null;
                        }
                    }
                }
            };
            timer.schedule(updateWidget, 0, 200);


        }
        stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "IntentService started");

    }



    protected PendingIntent getSpecificPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }



    public Bitmap decodeBitmap(String icon) {
        Bitmap iconBitmap = null;
        try {
            iconBitmap = BitmapFactory.decodeStream((InputStream) new URL("http://openweathermap.org/img/w/"
                    + icon + ".png").getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return iconBitmap;
    }


}
