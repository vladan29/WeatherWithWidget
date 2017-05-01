package com.apps.vladan.weather_widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, AppWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
        context.startService(intent);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences
                ("com.apps.vladan.weather_widget", MODE_PRIVATE);

        SharedPreferences.Editor edit = prefs.edit();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);


        if ((UpdateWidgetService.MORE).equals(intent.getAction())) {
            edit.putInt("state", 0);
            edit.putString("more_less", "Show less");
            edit.apply();

            ComponentName thisWidget = new ComponentName(context, AppWidget.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, AppWidgetManager.getInstance(context), allWidgetIds);
        }
        if ((UpdateWidgetService.LESS).equals(intent.getAction())) {
            edit.putInt("state", 1);
            edit.putString("more_less", "Show more");
            edit.apply();

            ComponentName thisWidget = new ComponentName(context, AppWidget.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, AppWidgetManager.getInstance(context), allWidgetIds);
        }


        super.onReceive(context, intent);
    }


}




