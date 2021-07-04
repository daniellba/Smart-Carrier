package com.example.smartcarrier;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

/*In this class I let the user set the distance between him and the carrier using radio buttons
* also, he may set a tune if the carrier is in a greater distance then what he set.*/
public class SettingsActivity extends AppCompatActivity {
    int DistanceValue = 1;
    TextView About_TXV;
    RadioButton RBTN_1_ID, RBTN_2_ID, RBTN_3_ID;
    Button dSet_Tune_BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings_screen);
        {
            About_TXV = (TextView)findViewById(R.id.About_TXV);
            About_TXV.setText("Maximum load up to 5Kg\nFollow Me button: start the carrier follow \nManual button: control the carrier manually.");
        }

        dSet_Tune_BTN = findViewById(R.id.Set_Tune_BTN);
        dSet_Tune_BTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    //Asking the user permission to access his external storage.
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
                    }
                    else
                    {
                        Intent intent = new Intent(SettingsActivity.this, SongListActivity.class);
                        startActivity(intent);
                        return;
                    }
                }
            }
        });
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

    //If the user pressed back button, he will be moving to main screen (MainActivity) not before closing the socket.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Distance set to " + DistanceValue + " Meter", Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.putExtra("Distance", DistanceValue);
        setResult(RESULT_OK, i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}