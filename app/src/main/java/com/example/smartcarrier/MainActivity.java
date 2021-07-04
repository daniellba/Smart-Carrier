package com.example.smartcarrier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;

/*My main activity, in this class I set a connection between the phone and the bluetooth device,
* extracting MY location from phone's GPS, sending the location- longitude and latitude to arduino,
* changing to ManualActivity, SettingsActivity screens and also implement reconnection button.*/

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    Button dFollow_Me_Btn, dManual_Btn, dSettings_Btn, dReconnect_Btn;
    private TextView GPS_Value;
    private static final int ONE_MINUTE = 1000 * 60 * 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3;

    //android studio libraries in order to extract my location
    LocationManager locationManager;
    Location currentBestLocation;
    LocationListener locationListener;

    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }
    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dFollow_Me_Btn = findViewById(R.id.Follow_Me_Btn);
        dManual_Btn = findViewById(R.id.Manual_Btn);
        dSettings_Btn = findViewById(R.id.Settings_Btn);
        dReconnect_Btn = findViewById(R.id.Reconnect_Btn);

        //define the TextView in order to write in the text box the cordenations.
        GPS_Value = findViewById(R.id.GPS_Value_ID);

        //function to set connection
        ConnectToBT();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }

        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location) {
                // New Location found by the network location provider
                if (isBetterLocation(location, currentBestLocation)){
                    currentBestLocation = location;
                    msg("New Location");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with location manager to start requesting location data
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else
            {
            initLocationRequest(locationListener);
        }

        dFollow_Me_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Pass that location to sending method
                if (currentBestLocation != null) {
                    //msg("Latitude: " + currentBestLocation.getLatitude() + "\nLongitude: " + currentBestLocation.getLongitude());
                    send(currentBestLocation);
                }else{
                    initLocationRequest(locationListener);
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
                    btSocket = null;
                    //System.out.println(btSocket.isConnected());
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
                try {
                    btSocket.close();
                    btSocket = null;
                    //System.out.println(btSocket.isConnected());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Since I need to retrieve data from SettingScreen I used "startActivityForResult"
                startActivityForResult(SettingScreen, 1);
            }
        });

        dReconnect_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (btSocket == null)
                {
                    ConnectToBT();
                    msg("Bluetooth is connected successfully.");
                }
                else
                {
                    msg("Bluetooth is already connected.");
                }
            }
        });
    }

    //In this function I check every few seconds (30s or so) for a new location to update the carrier for a new location.
    private boolean isBetterLocation(Location location, Location currBest)
    {
        if (currBest == null){
            // If we don't have a current best location, then this is it!
            return true;
        }

        // Check time of location recieved
        long timeDela = location.getTime() - currBest.getTime();
        boolean isSignificantlyNewer = timeDela > ONE_MINUTE;
        boolean isSignificantlyOlder = timeDela < -ONE_MINUTE;
        boolean isNewer = timeDela > 0;

        if (isSignificantlyNewer){
            // Location is newer by 1 minute! User probs moved to use it!!
            return true;
        }else if (isSignificantlyOlder){
            return false;
        }

        // Check for the given accuracy of location
        int accuracyDelta = (int) (location.getAccuracy() - currBest.getAccuracy());
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 10;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    // Checks whether two providers are the same
    private boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    //Phone location will be received by Arduino
    private void send(Location location)
    {
        if (btSocket!=null)
        {
            this.GPS_Value.setText(location.toString().substring(9,19));
            try
            {
                msg(("<" + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude()) + ">"));
                btSocket.getOutputStream().write(("<" + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude()) + ">").getBytes());
            }
            catch (IOException e)
            {
                msg("Error - sending data to socket.");
            }
        }
    }

    //Requesting location permissions.
    private void initLocationRequest(LocationListener locationListener)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            currentBestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    //Retrieve the distance between carrier and user from SettingsActivity
    void SettingsActivity(int requestCode, int resultCode, Intent data) throws IOException
    {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String myStr = data.getStringExtra("Distance");
                btSocket.getOutputStream().write(Integer.parseInt(myStr));
            }
        }
    }

    //Automatic bluetooth connection
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

    //When the user close the app.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            btSocket.close(); //close connection
        }
        catch (IOException e)
        { msg("Error - closing the socket.");}
        finish();
    }
}