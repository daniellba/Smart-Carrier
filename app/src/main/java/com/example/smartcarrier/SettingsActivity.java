package com.example.smartcarrier;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {
    int DistanceValue = 1;
    TextView About_TXV;
    RadioButton RBTN_1_ID, RBTN_2_ID, RBTN_3_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings_screen);
        {
            About_TXV = (TextView)findViewById(R.id.About_TXV);
            About_TXV.setText("Maximum load up to 5Kg\nFollow Me button: start the carrier follow \nManual button: control the carrier manually.");
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.RBTN_1_ID:
                if (checked)
                    DistanceValue = 1;
                    break;
            case R.id.RBTN_2_ID:
                if (checked)
                    DistanceValue = 2;
                    break;
            case R.id.RBTN_3_ID:
                if (checked)
                    DistanceValue = 3;
                    break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Distance set to " + DistanceValue + " Meter", Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.putExtra("Distance", DistanceValue);
        setResult(RESULT_OK, i);
    }
}