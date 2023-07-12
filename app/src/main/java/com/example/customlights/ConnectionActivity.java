package com.example.customlights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ConnectionActivity extends AppCompatActivity {

    public final static int MESSAGE_READ = 2;

    Button dicoverBtn, controlBtn, stopScanBtn;
    TextView statusText;
    ListView btDeviceslv;

    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothManager bTManager;
    private BluetoothAdapter bTAdapter;
    private ArrayAdapter<String> btArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        dicoverBtn = findViewById(R.id.discoverBtn);
        stopScanBtn = findViewById(R.id.stopScanBtn);
        controlBtn = findViewById(R.id.controlBtn);
        statusText = findViewById(R.id.statustxt);
        btDeviceslv = findViewById(R.id.btDevices);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ConnectionActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_ENABLE_BT);
            return;
        }

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bTAdapter = bluetoothManager.getAdapter();

        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();
        ArrayList<String> devices = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                devices.add(deviceName + "\n" + deviceHardwareAddress);
            }
        }

        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        btDeviceslv.setAdapter(btArrayAdapter);

        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ConnectionActivity.this,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_ENABLE_BT);
            ActivityCompat.requestPermissions(ConnectionActivity.this,
                    new String[]{android.Manifest.permission.BLUETOOTH},
                    REQUEST_ENABLE_BT);
            ActivityCompat.requestPermissions(ConnectionActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    REQUEST_ENABLE_BT);
            return;
        }
        startActivity(enableBTIntent);
// Connect to the device
        btDeviceslv.setOnItemClickListener(
                (parent, view, position, id) -> {
                    String deviceName = ((TextView) view).getText().toString();
                    String deviceAddress = deviceName.substring(deviceName.length() - 17);
                    Toast.makeText(getApplicationContext(), "Connecting to " + deviceName, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
                    intent.putExtra("deviceAddress", deviceAddress);
                    startActivity(intent);
                }
        );
    }
}