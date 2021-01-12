package com.example.smartcarrier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button dFollow_Me_Btn, dManual_Btn, dSettings_Btn;
    private TextView GPS_Value, Location_Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dFollow_Me_Btn = findViewById(R.id.Follow_Me_Btn);
        dManual_Btn = findViewById(R.id.Manual_Btn);
        dSettings_Btn = findViewById(R.id.Settings_Btn);

        dFollow_Me_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

            }
        });

        Intent ManualScreen = new Intent(this, ManualScreen.class);
        dManual_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(ManualScreen);
            }
        });


        Intent SettingScreen = new Intent(this, ManualScreen.class);
        dSettings_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent numberIntent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(numberIntent);
            }
        });

        //define the TextView in order to wirte in the box the cordenations.
        GPS_Value = findViewById(R.id.GPS_Value_ID);
        Location_Value = findViewById(R.id.Location_Value_ID);


    }
}