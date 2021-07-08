package com.example.smartcarrier;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/*In this class I implement manual carrier driving using his phone as a remote */
public class ManualScreen extends AppCompatActivity {
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    Button Forward_BTN, Backward_BTN, Left_BTN, Right_BTN;

    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }
    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Forward_BTN = findViewById(R.id.Forward_BTN);
        Backward_BTN = findViewById(R.id.Backward_BTN);
        Left_BTN = findViewById(R.id.Left_BTN);
        Right_BTN = findViewById(R.id.Right_BTN);

        ConnectToBT();

        //Those four buttons you can see below are sending diffrent number to arduino, each number is set
        //to a diffrent direction, for example, number 1 will move both motors will lead to drive forward.
        Forward_BTN.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    OutputStream outputStream = btSocket.getOutputStream();
                    outputStream.write(1);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        Backward_BTN.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {

                    OutputStream outputStream = btSocket.getOutputStream();
                    outputStream.write(0);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        Right_BTN.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {

                    OutputStream outputStream = btSocket.getOutputStream();
                    outputStream.write(2);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        Left_BTN.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {

                    OutputStream outputStream = btSocket.getOutputStream();
                    outputStream.write(3);
                    System.out.println(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    //Same method from MainActivity class.
    public void ConnectToBT()
    {
        this.btSocket = btSocket;
        this.btAdapter = btAdapter;
        final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        btAdapter = BluetoothAdapter.getDefaultAdapter(); //Bluetooth definition
        //    System.out.println(btAdapter.getBondedDevices());
        BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:51:F5:B4:73"); //Connect to my hc-05 via mac adress
        //   System.out.println(hc05.getName());

        //Creating the socket, if fail, try twice more
        int counter = 0;
        do {
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
    }

    //If the user pressed back button, he will be moving to main screen (MainActivity) not before closing the socket.
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        try
        {
            OutputStream outputStream = btSocket.getOutputStream();
            outputStream.write(-1);
            System.out.println(outputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            btSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //If the user closed the app.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            btSocket.close(); //close connection
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(),"Error - closing the socket.",Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
