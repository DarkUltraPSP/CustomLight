package com.example.customlights;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class BluetoothThread extends Thread {
    private final static int REQUEST_ENABLE_BT = 1;
    private final BluetoothManager bluetoothManager;
    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothDevice device;
    private BluetoothSocket socket;
    Context context;
    Activity activity;

    public BluetoothThread(BluetoothDevice device, Context context, Activity activity) {
        this.bluetoothManager = getSystemService(context, BluetoothManager.class);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.device = device;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void run() {
        BluetoothSocket tmp = null;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID pour le profil SPP (Serial Port Profile)

        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_ENABLE_BT);
                return;
            }
            tmp = device.createRfcommSocketToServiceRecord(uuid);
            socket = tmp;
            socket.connect();
            Log.d("BluetoothThread", "Connexion établie avec succès !");
            // Commencez à gérer la communication Bluetooth ici
        } catch (IOException e) {
            Log.e("BluetoothThread", "Erreur lors de la connexion : " + e.getMessage());
            // Gérez les erreurs de connexion ici
        }
    }

    public void write(String data) {
        try {
            socket.getOutputStream().write(data.getBytes());
        } catch (IOException e) {
            Log.e("BluetoothThread", "Erreur lors de l'envoi de données : " + e.getMessage());
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("BluetoothThread", "Erreur lors de la fermeture du socket : " + e.getMessage());
        }
    }
}
