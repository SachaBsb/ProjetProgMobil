package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int REQUEST_ENABLE_BT = 1;
    private static BluetoothManager bluetoothManager;
    private static BluetoothAdapter bluetoothAdapter;

    public static BluetoothAdapter getBluetoothAdapterObject() {
        return bluetoothAdapter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        Button btn_serveur = findViewById(R.id.btn_serveur);
        btn_serveur.setOnClickListener(this);

        Button btn_client = findViewById(R.id.btn_client);
        btn_client.setOnClickListener(this);
    }


    @SuppressLint("MissingPermission")
    public void onClick(View v) {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "This device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
            // Device doesn't support Bluetooth
        }
        switch (v.getId()) {
            case R.id.btn_serveur:
                Intent serverIntent = new Intent(this, ServerActivity.class); // Création d’une intention
                // serverIntent.putExtra(bluetoothAdapter, bluetoothAdapter); // Ajout d’un parametre à l’intention
                startActivity(serverIntent);
                break;
            case R.id.btn_client:
                Intent clientIntent = new Intent(this, ClientActivity.class); // Création d’une intention
                startActivity(clientIntent);
                break;
        }
    }
}