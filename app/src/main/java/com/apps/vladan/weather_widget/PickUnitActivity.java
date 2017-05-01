package com.apps.vladan.weather_widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class

PickUnitActivity extends AppCompatActivity {
    Spinner units;
    String unit;
    Intent startUnitIntent;
    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_unit);

        Intent fetchIntent=getIntent();
        flag=fetchIntent.getBooleanExtra("flag",false);

        startUnitIntent = new Intent(getApplicationContext(), AppWidget.class);


        units = (Spinner) findViewById(R.id.units_spinner);
        ArrayAdapter<CharSequence> unitsAdapter = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        units.setAdapter(unitsAdapter);
        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        unit = "°C";
                        break;
                    case 1:
                        unit = "F";
                        break;
                }
                SharedPreferences prefs = getApplicationContext().getSharedPreferences
                        ("com.apps.vladan.weather_widget", MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Unit", unit);
                edit.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button setUnitButton = (Button) findViewById(R.id.set_unit_button);
        setUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag){

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("unit",unit);
                    setResult(ConfigureActivity.RESULT_OK,returnIntent);
                }
                startUnitIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = AppWidgetManager.getInstance(getApplication())
                        .getAppWidgetIds(new ComponentName(getApplication(), AppWidget.class));
                startUnitIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(startUnitIntent);
                finish();
            }
        });

    }
}
