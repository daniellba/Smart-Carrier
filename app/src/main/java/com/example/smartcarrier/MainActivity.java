package com.example.smartcarrier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter= null;
    BluetoothSocket btSocket = null;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Button dFollow_Me_Btn, dManual_Btn, dSettings_Btn;
    private TextView GPS_Value, Location_Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dFollow_Me_Btn = findViewById(R.id.Follow_Me_Btn);
        dManual_Btn = findViewById(R.id.Manual_Btn);
        dSettings_Btn = findViewById(R.id.Settings_Btn);

        btAdapter = BluetoothAdapter.getDefaultAdapter(); //Bluetooth definition
        //    System.out.println(btAdapter.getBondedDevices());
        BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:51:F5:B4:73"); //connect to my hc-05 via mac adress
        //   System.out.println(hc05.getName());

        int counter = 0;
        do {    //creating the socket, if fail, try twice more
            try {
                btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                System.out.println(btSocket);
                btSocket.connect();
                System.out.println(btSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        } while (!btSocket.isConnected() && counter < 3);

        try {
            if (btSocket == null) {
                btAdapter = BluetoothAdapter.getDefaultAdapter();

                btSocket = hc05.createInsecureRfcommSocketToServiceRecord(mUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        BluetoothSocket finalBtSocket = btSocket;

        dFollow_Me_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    OutputStream outputStream = finalBtSocket.getOutputStream();
                    outputStream.write(4);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Intent ManualScreen = new Intent(this, ManualScreen.class);
        dManual_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    btSocket.close();
                    System.out.println(btSocket.isConnected());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(ManualScreen);
            }
        });


        Intent SettingScreen = new Intent(this, SettingsActivity.class);
        dSettings_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Intent numberIntent = new Intent(MainActivity.this,SettingsActivity.class);
                try {
                    btSocket.close();
                    System.out.println(btSocket.isConnected());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(SettingScreen);
            }
        });

        //define the TextView in order to wirte in the box the cordenations.
        GPS_Value = findViewById(R.id.GPS_Value_ID);
        Location_Value = findViewById(R.id.Location_Value_ID);
    }
}