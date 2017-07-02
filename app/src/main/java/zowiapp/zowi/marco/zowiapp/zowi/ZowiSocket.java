package zowiapp.zowi.marco.zowiapp.zowi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import zowiapp.zowi.marco.zowiapp.MainActivity;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.utils.Layout;

public class ZowiSocket {

    private MainActivity mainActivity;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver bluetoothReceiver;
    private static OutputStream outputStream;
    private static InputStream inputStream;

    private static final String ZOWI_PROGRAM_ID = "SUPER_ZOWI";
    private static final int REQUEST_COARSE_LOCATION = 2;
    private static final String ZOWI_NAME = "Zowi";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int BOND_NONE = 10;

    private boolean killThread = false;

    public ZowiSocket(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void connectToZowi() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean bluetoothAvailable = checkBluetoothConnectivity(bluetoothAdapter);

        if (bluetoothAvailable) {
            checkLocationPermission();
        }
    }

    private boolean checkBluetoothConnectivity(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            Log.i("ConnectionStep", "Bluetooth no disponible");
            return false;
        }
        else if (!bluetoothAdapter.isEnabled()) {
            Log.i("ConnectionStep", "Bluetooth habilitado");
            bluetoothAdapter.enable();
        }
        Log.i("ConnectionStep", "Blutooth disponible");
        return true;
    }

    private void checkLocationPermission() {
        int accessCoarseLocation = ContextCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            Log.i("ConnectionStep", "Solicitando permismos de localización");
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        }
        else {
            String zowiAddress = getZowiAddress();
            if (zowiAddress.equals("")) {

                startDiscovery();
            }
            else {
                Log.i("ConnectionStep", "Conectando dispositivo");
                connectDevice(zowiAddress);
            }
        }
    }

    private String getZowiAddress() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Log.i("ConnectionStep", "getZowiAddress");

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Log.i("ConnectionStep", device.getName());
                if (device.getName().equals(ZOWI_NAME)) {
                    Log.i("ConnectionStep", "Obtenida dirección de Zowi");
                    return device.getAddress();
                }
            }
        }
        return "";
    }

    public void startDiscovery() {
        bluetoothReceiver = new BluetoothReceiver();

        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        mainActivity.registerReceiver(bluetoothReceiver, bluetoothFilter);

        bluetoothAdapter.startDiscovery();
    }

    private void connectDevice(String zowiAddress) {
        BluetoothDevice zowiDevice = bluetoothAdapter.getRemoteDevice(zowiAddress);
        try {
            BluetoothSocket bluetoothSocket = zowiDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            Log.i("connectDevice", "Creando hilo");
            new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !killThread) {
                        int bytesAvailable = ZowiSocket.isInputStreamAvailable();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            ZowiSocket.readInputStream(packetBytes);

                            String receivedText = new String(packetBytes, 0, bytesAvailable);
                            if (receivedText.contains(ZOWI_PROGRAM_ID)) {
                                Zowi.setConnected(true);

                                Layout.closeProgressDialog();
                                Layout.drawOverlay(mainActivity, mainActivity.findViewById(R.id.main_activity_container));
                                Log.i("connectDevice", "ZowiSocket conectado");

                                killThread = true;
                            }

                        }
                    }
                }
            }).start();
        }
        catch (IOException e) {
            Layout.drawAlertDialog(mainActivity);
            e.printStackTrace();
        }
    }

    public static void sendCommand(String command) {
        char r = '\r';

        try {
            outputStream.write(command.getBytes());
            outputStream.write(r);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int isInputStreamAvailable() {
        try {
            return inputStream.available();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int readInputStream(byte[] bytes) {
        try {
            return inputStream.read(bytes);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            BluetoothDevice device;
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i("BluetoothReceiver", "Discovery started");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i("BluetoothReceiver", "Discovery finished");
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getBondState() == BOND_NONE) {
                        Layout.drawAlertDialog(mainActivity);
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getName().equals(ZOWI_NAME)) {
                        bluetoothAdapter.cancelDiscovery();
                        device.createBond();
                    }
                    Log.i("BluetoothReceiver", "Device discovered " + device.getName() + ": " + device.getAddress());
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    int bondState = device.getBondState();

                    if (name.equals(ZOWI_NAME) && (bondState == BluetoothDevice.BOND_BONDED)) {
                        connectDevice(device.getAddress());
                    }
                    Log.i("BluetoothReceiver", "Device bond changed " + device.getName() + ": " + device.getBondState());
                    break;
            }
        }
    }
}
