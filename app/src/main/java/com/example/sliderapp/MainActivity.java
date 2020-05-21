package com.example.sliderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private SeekBar simpleSeekBar;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;

    private DataOutputStream os;
    private InputStream is;
    private BluetoothSocket socket;

    private String valString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        simpleSeekBar = (SeekBar) findViewById(R.id.seekBar);



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

                valString = String.format("%d\n", progressChangedValue);
//                byte[] valBytes = val.getBytes();

            }


            BroadcastReceiver discoveryResult = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                    BluetoothDevice remoteDevice;

                    remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Toast.makeText(getApplicationContext(), "Discovered: " + remoteDeviceName +
                            " address " + remoteDevice.getAddress(), Toast.LENGTH_SHORT).show();

                    try{
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(remoteDevice.getAddress());

                        Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});

                        BluetoothSocket clientSocket =  (BluetoothSocket) m.invoke(device, 1);

                        clientSocket.connect();

                        os = new DataOutputStream(clientSocket.getOutputStream());

                        new clientSock().start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("BLUETOOTH", e.getMessage());
                    }

                }
            };

        });



    }

    public class clientSock extends Thread {
        public void run() {
            try {
                os.writeBytes(valString); // anything you want
                os.flush();
            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }
    }


}
