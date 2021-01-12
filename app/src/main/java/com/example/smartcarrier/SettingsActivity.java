package com.example.smartcarrier;

import android.os.Bundle;
import android.content.Intent;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    TextView About_TXV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        {
            About_TXV = (TextView)findViewById(R.id.About_TXV);
            About_TXV.setText("Maximum load up to 5Kg\nFollow Me button: start the carrier follow \nManual button: control the carrier manually.");
        }
    }
}