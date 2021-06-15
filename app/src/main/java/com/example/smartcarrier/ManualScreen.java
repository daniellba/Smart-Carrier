package com.example.smartcarrier;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ManualScreen extends AppCompatActivity {
    BluetoothAdapter btAdapter= null;
    BluetoothSocket btSocket = null;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Button Forward_BTN, Backward_BTN, Left_BTN, Right_BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_screen);

        Forward_BTN = findViewById(R.id.Forward_BTN);
        Backward_BTN = findViewById(R.id.Backward_BTN);
        Left_BTN = findViewById(R.id.Left_BTN);
        Right_BTN = findViewById(R.id.Right_BTN);

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        BluetoothSocket finalBtSocket = btSocket;

 /*       Forward_BTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    OutputStream outputStream = finalBtSocket.getOutputStream();
                    outputStream.write(1);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });


        Backward_BTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {

                    OutputStream outputStream = finalBtSocket.getOutputStream();
                    outputStream.write(0);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        Right_BTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {

                    OutputStream outputStream = finalBtSocket.getOutputStream();
                    outputStream.write(2);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        Left_BTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {

                    OutputStream outputStream = finalBtSocket.getOutputStream();
                    outputStream.write(3);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
*/

        Forward_BTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    OutputStream outputStream = finalBtSocket.getOutputStream();
                    outputStream.write(1);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//     InputStream inputStream = null; //receiving BT data
//        try {
//            inputStream = btSocket.getInputStream();
//            inputStream.skip(inputStream.available());
//
//            for (int i = 0; i < 26; i++) {
//
//                byte b = (byte) inputStream.read();
//                System.out.println((char) b);
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
        //if the user pressed back button
        @Override
        public void onBackPressed(){
            System.out.println("back pressed");
            super.onBackPressed();
            try {
                btSocket.close();
                System.out.println(btSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
