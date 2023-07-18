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

public class MainActivity extends Activity{

    ImageView colorWheel;
    TextView redText, greenText, blueText, brightnessTv, speedTv;
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

        colorWheel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                    bitmap = colorWheel.getDrawingCache();
                    if (motionEvent.getX() > 0 && motionEvent.getX() < bitmap.getWidth() && motionEvent.getY() > 0 && motionEvent.getY() < bitmap.getHeight()){
                        int pixel = bitmap.getPixel((int)motionEvent.getX(), (int)motionEvent.getY());

                        int redValue = (pixel >> 16) & 0xff;
                        int greenValue = (pixel >> 8) & 0xff;
                        int blueValue = pixel & 0xff;

                        redText.setText("Red: " + redValue);
                        greenText.setText("Green: " + greenValue);
                        blueText.setText("Blue: " + blueValue);

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

        speedSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speedTv.setText("Speed : " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Connexion to the bluetooth device
        if (deviceAddress != null){
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            connectedBluetoothDevice(device, this, this);
        }

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chip chip = findViewById(modeChipGroup.getCheckedChipId());
                String mode = chip.getText().toString().toLowerCase();
                int brightness = brightSb.getProgress();
                int speed = speedSb.getProgress();
                int red = Integer.parseInt(redText.getText().toString().split(" ")[1]);
                int green = Integer.parseInt(greenText.getText().toString().split(" ")[1]);
                int blue = Integer.parseInt(blueText.getText().toString().split(" ")[1]);

                Mode m = new Mode(mode, brightness, speed, red, green, blue);
                System.out.println(m.toJSON());
                sendBluetoothData(m.toJSON());
            }
        });
    }

    private void connectedBluetoothDevice(BluetoothDevice device, Context context, Activity activity){
        bluetoothThread = new BluetoothThread(device, context, activity);
        bluetoothThread.start();
    }

    private void sendBluetoothData(String data) {
        if (bluetoothThread != null) {
            bluetoothThread.write(data);
        }
    }
}