package com.example.customlights;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.customlights.Model.Mode;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class MainActivity extends Activity {

    ImageView colorWheel;
    TextView redText, greenText, blueText, redValue, greenValue, blueValue, brightnessTv, speedTv;
    View mColorView;
    Button applyBtn;
    Bitmap bitmap;
    ChipGroup modeChipGroup;
    SeekBar brightSb, speedSb;

    BluetoothThread bluetoothThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        String deviceAddress = getIntent().getStringExtra("deviceAddress");

        colorWheel = findViewById(R.id.colorWheel);
        redText = findViewById(R.id.redText);
        greenText = findViewById(R.id.greenText);
        blueText = findViewById(R.id.blueText);
        redValue = findViewById(R.id.redValue);
        greenValue = findViewById(R.id.greenValue);
        blueValue = findViewById(R.id.blueValue);
        mColorView = findViewById(R.id.displayColors);
        applyBtn = findViewById(R.id.applyBtn);
        modeChipGroup = findViewById(R.id.modeChipGroup);
        brightSb = findViewById(R.id.brightSb);
        speedSb = findViewById(R.id.speedSb);
        brightnessTv = findViewById(R.id.brightnessTv);
        speedTv = findViewById(R.id.speedTv);

        colorWheel.setHasTransientState(true);
        colorWheel.setDrawingCacheEnabled(true);
        colorWheel.buildDrawingCache(true);

        int red = 0;
        int green = 0;
        int blue = 0;

        //Connexion to the bluetooth device
        if (deviceAddress != null) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            connectedBluetoothDevice(device, this, this);
        }

        colorWheel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    bitmap = colorWheel.getDrawingCache();
                    if (motionEvent.getX() > 0 && motionEvent.getX() < bitmap.getWidth() && motionEvent.getY() > 0 && motionEvent.getY() < bitmap.getHeight()) {
                        int pixel = bitmap.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());

                        int red = (pixel >> 16) & 0xff;
                        int green = (pixel >> 8) & 0xff;
                        int blue = pixel & 0xff;

                        redValue.setText(String.valueOf(red));
                        greenValue.setText(String.valueOf(green));
                        blueValue.setText(String.valueOf(blue));

                        mColorView.setBackgroundColor(pixel);
                    }
                }
                return true;
            }
        });

        brightSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightnessTv.setText("Brightness : " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        modeChipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                Chip chip = findViewById(modeChipGroup.getCheckedChipId());
                if (chip == null) return;
                Toast.makeText(MainActivity.this, chip.getText(), Toast.LENGTH_SHORT).show();
                if (chip.getText().toString().equals("RGBFlow")) {
                    speedSb.setMax(20);
                } else {
                    speedSb.setMax(255);
                }
            }
        });



        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chip chip = findViewById(modeChipGroup.getCheckedChipId());
                if (chip == null) {
                    Toast.makeText(MainActivity.this, "Please select a mode", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode = chip.getText().toString().toLowerCase();
                int brightness = brightSb.getProgress();
                int speed = speedSb.getProgress();
                int red = Integer.parseInt(redValue.getText().toString());
                int green = Integer.parseInt(greenValue.getText().toString());
                int blue = Integer.parseInt(blueValue.getText().toString());

                Mode m = new Mode(mode, speed, brightness, red, green, blue);
                System.out.println(m.toJSON());
                sendBluetoothData(m.toJSON());
            }
        });
    }

    private void connectedBluetoothDevice(BluetoothDevice device, Context context, Activity activity) {
        bluetoothThread = new BluetoothThread(device, context, activity);
        bluetoothThread.start();
    }

    private void sendBluetoothData(String data) {
        if (bluetoothThread != null) {
            bluetoothThread.write(data);
        }
    }
}