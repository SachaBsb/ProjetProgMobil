package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.*;
import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;
import static com.example.projet.MainActivity.REQUEST_ENABLE_BT;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothServerSocket bss;
    private static BluetoothSocket bs;
    private static BluetoothSocket get_bs() { return bs;}

    private static final UUID uuid = UUID.fromString("9226f308-405b-42ff-afb3-fbfee5d24f5a");
    public static UUID get_uuid() { return uuid;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        Button btn_serveur = findViewById(R.id.btn_send_data);
        btn_serveur.setOnClickListener((View.OnClickListener) this);

        BluetoothAdapter bluetoothAdapter = MainActivity.getBluetoothAdapterObject();

        if (!bluetoothAdapter.isEnabled()) { // Enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) to handle the case where the user grants the permission. See the documentation for ActivityCompat#requestPermissions for more details.
                return;
            }

            bss = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MonServeur", uuid);

            while (true) {

                bs = bss.accept();
                if (bs != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    // manageMyConnectedSocket(bs);
                    bss.close();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* Bouton qu'on utilise pour envoyer Hello World */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_data:
                // On cree une instance de MyBluetoothService
                MyBluetoothService bluetoothService = new MyBluetoothService();
                // connection client server
                BluetoothSocket socket = get_bs();
                // get connectedThread ref for bluetooth connection
                MyBluetoothService.ConnectedThread connectedThread = bluetoothService.new ConnectedThread(socket);
                // Execute connectdThread
                connectedThread.start();
                // create and send Hello World
                String message = "Hello World";
                byte[] messageBytes = message.getBytes();
                // Envoyez le message au client via Bluetooth
                connectedThread.write(messageBytes);
        }
    }

}