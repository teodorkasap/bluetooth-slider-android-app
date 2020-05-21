package com.example.sliderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    SeekBar simpleSeekBar;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mDevice;

    private OutputStream os;
    private InputStream is;
    private BluetoothSocket socket;

    private static final String APP_NAME = "BTLED";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // instantiate seekBar
        simpleSeekBar = (SeekBar) findViewById(R.id.seekBar);
        // listener for changes in seekBar





        if (mBluetoothAdapter == null) {

            Toast.makeText(MainActivity.this, "Device does not support Bluetooth",
                    Toast.LENGTH_SHORT).show();
        } else if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        if (mBluetoothAdapter.getBondedDevices() != null) {

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    mDevice = device;
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "You must pair your device first",
                    Toast.LENGTH_SHORT).show();
        }


        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();

                String val = String.format("%d\n", progressChangedValue);
                byte[] valBytes = val.getBytes();
                write(valBytes);
            }

            private void write(byte[] ch) {

                try {

                    socket.connect();
                    if (socket.isConnected()){
                        for (int k = 0; k < ch.length; k++) {
                            new DataOutputStream(socket.getOutputStream()).writeByte(ch[k]);
                        }
                    } else {
                        System.out.println("no socket connection present...");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void run() {
                final int BUFFER_SIZE = 1024;
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytes = 0;
                int b = BUFFER_SIZE;

                while (true){
                    try {
                        bytes = socket.getInputStream().read(buffer, bytes, BUFFER_SIZE - bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });



    }


}
