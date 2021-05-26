package com.example.smartcarrier;
//package net.bane.bluetoothapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ManualScreen extends AppCompatActivity {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_screen);

        Button Forward_BTN, Backward_BTN, Left_BTN, Right_BTN;

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter(); //Bluetooth definition
        //System.out.println(btAdapter.getBondedDevices());

        BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:51:F5:B4:73"); //connect to my hc-05 via mac adress
        //System.out.println(hc05.getName());

        BluetoothSocket btSocket = null;
        int counter = 0;
        do {    //creating the socket, if fail, try twice more
            try {
                btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                //System.out.println(btSocket);
                btSocket.connect();
                //System.out.println(btSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        } while (!btSocket.isConnected() && counter < 3);


        try {
            OutputStream outputStream = btSocket.getOutputStream(); //sending data to BT
            outputStream.write(48);/////////////////////////////657777777
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream = null; //receiving BT data
        try {
            inputStream = btSocket.getInputStream();
            inputStream.skip(inputStream.available());

            for (int i = 0; i < 26; i++) {

                byte b = (byte) inputStream.read(); //reading the data byte by byte
                System.out.println((char) b);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            btSocket.close();
            System.out.println(btSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
