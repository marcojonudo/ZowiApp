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
import android.os.AsyncTask;
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
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler.ThreadType;

public class ZowiSocket {

    private static MainActivity mainActivity;

    private static BluetoothAdapter bluetoothAdapter;
    private static OutputStream outputStream;
    private static InputStream inputStream;
    private static BluetoothSocket bluetoothSocket;
    private static BluetoothReceiver disconnectionReceiver;

    public static final String ZOWI_PROGRAM_ID = "SUPER_ZOWI";
    private static final int REQUEST_COARSE_LOCATION = 2;
    private static final String ZOWI_NAME = "Zowi";
    private static final String END_COMMAND_IDENTIFIER = "%%";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int BOND_NONE = 10;
    private static StringBuilder zowiReceivedText = new StringBuilder();

    public ZowiSocket(MainActivity mainActivityParam) {
        mainActivity = mainActivityParam;
    }

    public BluetoothReceiver getDisconnectionReceiver() {
        return disconnectionReceiver;
    }

    public static void closeConnection() {
        try {
            bluetoothSocket.close();
            inputStream.close();
            outputStream.close();
            Zowi.setConnected(false);
            mainActivity.unregisterReceiver(disconnectionReceiver);

            Log.i("bluetoothConnection", "closeConnection");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectToZowi() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean bluetoothAvailable = checkBluetoothConnectivity(bluetoothAdapter);

        if (bluetoothAvailable) {
            boolean locationPermission = checkLocationPermission();

            if (locationPermission) {
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
    }

    private static boolean checkBluetoothConnectivity(BluetoothAdapter bluetoothAdapter) {
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

    private boolean checkLocationPermission() {
        int accessCoarseLocation = ContextCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            Log.i("ConnectionStep", "Solicitando permismos de localización");
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
            return false;
        }
        else {
            return true;
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
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();

        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        mainActivity.registerReceiver(bluetoothReceiver, bluetoothFilter);

        bluetoothAdapter.startDiscovery();
    }

    private void registerDisconnectionReceiver() {
        disconnectionReceiver = new BluetoothReceiver();

        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        mainActivity.registerReceiver(disconnectionReceiver, bluetoothFilter);
    }

    private void connectDevice(String zowiAddress) {
        new BluetoothConnection().execute(zowiAddress);
    }

    public static void setConnected() {
        Zowi.setConnected(true);

        Layout.closeProgressDialog();
        Layout.drawOverlay(mainActivity, mainActivity.findViewById(R.id.main_activity_container));
        Log.i("connectDevice", "ZowiSocket conectado");
    }

    private static void setDisconnected() {
        Zowi.setConnected(false);

        Layout.closeProgressDialog();
        Layout.drawOverlay(mainActivity, mainActivity.findViewById(R.id.main_activity_container));
        Log.i("connectDevice", "ZowiSocket conectado");
    }

    static void sendCommand(String command) {
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

    public static boolean isConnected() {
        return bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    public static String readInputStream() {
        int bytesAvailable = isInputStreamAvailable();
        String receivedText = storeString(bytesAvailable);

        while (!receivedText.contains(END_COMMAND_IDENTIFIER)) {
            bytesAvailable = isInputStreamAvailable();
            receivedText = storeString(bytesAvailable);
        }

        zowiReceivedText = new StringBuilder();
        Log.i("readInputStream", receivedText);
        return receivedText;
    }

    private static String storeString(int bytesAvailable) {
        try {
            byte[] receivedFragment = new byte[bytesAvailable];
            inputStream.read(receivedFragment);
            zowiReceivedText.append(new String(receivedFragment, 0, receivedFragment.length));

            return zowiReceivedText.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return "";
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

                    if (device == null || device.getBondState() == BOND_NONE) {
                        Layout.drawAlertDialog(mainActivity);
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getName() != null && device.getName().equals(ZOWI_NAME)) {
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
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    setDisconnected();
                    // TODO Revisar
                    Layout.drawAlertDialog(mainActivity);
                    break;
            }
        }
    }

    private class BluetoothConnection extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            BluetoothDevice zowiDevice = Zowi.getBluetoothDevice(bluetoothAdapter, params[0]);
            try {
                bluetoothSocket = zowiDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();

                registerDisconnectionReceiver();

                Thread zowiConnectThread = ThreadHandler.createThread(ThreadType.ZOWI_CONNECTED);
                zowiConnectThread.start();

                return true;
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean connected) {
            if (!connected)
                Layout.drawAlertDialog(mainActivity);
        }

    }

}
