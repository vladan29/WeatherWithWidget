package com.apps.vladan.weather_widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

public class PickCityActivity extends AppCompatActivity {
    public String TAG = "tag";
    String city;
    View autocomplete;
    Intent startIntent;
    Boolean flag = false;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    long curentTime;
    BroadcastReceiver brc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_city);


        curentTime = System.currentTimeMillis() / 1000;

        autocomplete = findViewById(R.id.place_fragment);
        Intent fetchIntent = getIntent();
        flag = fetchIntent.getBooleanExtra("flag", false);

        findPlace(autocomplete);

        startIntent = new Intent(getApplicationContext(), AppWidget.class);
        prefs = getApplicationContext().getSharedPreferences
                ("com.apps.vladan.weather_widget", MODE_PRIVATE);


    }

    public void findPlace(View view) {
        try {

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            Intent intent =
                    new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "Connection error");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "Google error");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng cord = place.getLatLng();
                String placeCord = cord.toString().substring(10, cord.toString().length() - 1);

                city = place.getName().toString();
                edit = prefs.edit();
                edit.putString("Place", city);
                edit.putString("placeCord", placeCord);
                edit.apply();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "result canceld");

            }

            if (flag) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("city", city);
                returnIntent.putExtra("flag_city", 1);
                setResult(ConfigureActivity.RESULT_OK, returnIntent);
                finish();
            }

            startIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(getApplication())
                    .getAppWidgetIds(new ComponentName(getApplication(), AppWidget.class));
            startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            sendBroadcast(startIntent);
            finish();

        }


    }

    @Override
    protected void onDestroy() {
        try {
            if (brc!=null)
                unregisterReceiver(brc);
            brc=null;
        }catch (IllegalArgumentException e){
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }
}
