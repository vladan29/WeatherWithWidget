package com.apps.vladan.weather_widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigureActivity extends AppCompatActivity {


    private static final int PICK_CITY_ACTIVITY_RESULT_CODE = 0;
    private static final int PICK_UNIT_ACTIVITY_RESULT_CODE = 1;
    TextView city_name;
    TextView unit_view;
    int appWidgetId;
    int flag_city=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        setResult(RESULT_CANCELED);

        Button setButton = (Button) findViewById(R.id.set_button);
        Button placeButton = (Button) findViewById(R.id.place_button);
        Button unitButton = (Button) findViewById(R.id.unit_button);
        city_name = (TextView) findViewById(R.id.city_name);
        unit_view = (TextView) findViewById(R.id.unit_view);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            placeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent pickPlaceIntent = new Intent(getApplicationContext(), PickCityActivity.class);
                    pickPlaceIntent.putExtra("flag", true);
                    startActivityForResult(pickPlaceIntent, PICK_CITY_ACTIVITY_RESULT_CODE);
                }
            });

            unitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pickUnitIntent = new Intent(getApplicationContext(), PickUnitActivity.class);
                    pickUnitIntent.putExtra("flag", true);
                    startActivityForResult(pickUnitIntent, PICK_UNIT_ACTIVITY_RESULT_CODE);
                }
            });


            setButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (flag_city==1){
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Please set desired location",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CITY_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                String returnCityString = data.getStringExtra("city");
                flag_city=data.getIntExtra("flag_city",0);
                city_name.setText(returnCityString);
            }
        }
        if (requestCode == PICK_UNIT_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                String returnUnitString = data.getStringExtra("unit");
                unit_view.setText(returnUnitString);
            }

        }
    }
}
