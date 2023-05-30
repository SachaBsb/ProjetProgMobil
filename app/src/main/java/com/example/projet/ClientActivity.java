package com.example.projet;

import static com.example.projet.MainActivity.REQUEST_ENABLE_BT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ClientActivity extends AppCompatActivity {

    private BluetoothDevice mmDevice;
    public String targetedDeviceMacAdress = "18:87:40:78:ed:e8";
    public final UUID uuid = ServerActivity.get_uuid();
    private static BluetoothSocket bs;
    private static BluetoothSocket get_bs() {return bs;}

    private final InputStream is = null;
    private final OutputStream os = null;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        BluetoothAdapter bluetoothAdapter = MainActivity.getBluetoothAdapterObject();

        if (!bluetoothAdapter.isEnabled()) { // Enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) to handle the case where the user grants the permission. See the documentation for ActivityCompat#requestPermissions for more details.
            return;
        }

        connectToServer(bluetoothAdapter);
        try {
            transferData(bs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressLint("MissingPermission")
    public void connectToServer(BluetoothAdapter bluetoothAdapter) {
        @SuppressLint("MissingPermission")
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        try {
            for (BluetoothDevice device : pairedDevices) {
                // String deviceName = device.getName();
                String remoteDeviceMacAddress = device.getAddress(); // Mac Adress
                if (remoteDeviceMacAddress.equals(targetedDeviceMacAdress)) {
                    BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(remoteDeviceMacAddress);
                    bs = remoteDevice.createRfcommSocketToServiceRecord(uuid);
                    bs.connect();
                    return;
                }
            }
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        // boolean discovering = bluetoothAdapter.startDiscovery();
    }

    /*Reception de Hello World*/
    public void transferData(BluetoothSocket bs) throws IOException {
        // Instance of MyBluetoothService
        MyBluetoothService bluetoothService = new MyBluetoothService();
        // Connection client/server
        BluetoothSocket socket = get_bs();
        // Obtenez une référence vers ConnectedThread pour la connexion Bluetooth
        MyBluetoothService.ConnectedThread connectedThread = bluetoothService.new ConnectedThread(socket);
        // Launch thread
        connectedThread.start();
        // Handler to receive messages from thread
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MyBluetoothService.MessageConstants.MESSAGE_READ: // Le message a été recue
                        byte[] buffer = (byte[]) msg.obj;
                        int numBytes = msg.arg1;
                        String receivedMessage = new String(buffer, 0, numBytes);
                        final TextView data_received = (TextView) findViewById(R.id.data_received);
                        data_received.setText(receivedMessage);
                        break;
                    /*
                    case MyBluetoothService.MessageConstants.MESSAGE_WRITE: // Le message a été envoyé
                        break;
                    */
                    case MyBluetoothService.MessageConstants.MESSAGE_TOAST: // Une erreur lors de l'envoi du message
                        String error = msg.getData().getString("toast");
                        break;
                }
            }
        };
    }
}

