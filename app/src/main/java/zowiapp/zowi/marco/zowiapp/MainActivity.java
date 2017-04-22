package zowiapp.zowi.marco.zowiapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.activities.CheckerTemplate;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver bluetoothReceiver;
    private static OutputStream outputStream;

    private static final int REQUEST_COARSE_LOCATION = 2;
    private static final String ZOWI_NAME = "Zowi";
    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        boolean bluetoothAvailable = checkBluetoothConnectivity(bluetoothAdapter);
//
//        if (bluetoothAvailable) {
//            checkLocationPermission();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private boolean checkBluetoothConnectivity(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            Log.i("checkBluetoothConnectivity", "Bluetooth no disponible");
            return false;
        }
        else if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        return true;
    }

    private void checkLocationPermission() {
        int accessCoarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        }
        else {
            String zowiAddress = getZowiAddress();
            if (zowiAddress.equals("")) {
                startDiscovery();
            }
            else {
                Log.i("checkLocationPermission", "Conectando dispositivo...");
                connectDevice(zowiAddress);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDiscovery();
                }
                break;
            default:
                break;
        }
    }

    private String getZowiAddress() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(ZOWI_NAME)) {
                    return device.getAddress();
                }
            }
        }
        return "";
    }

    private void startDiscovery() {
        bluetoothReceiver = new BluetoothReceiver();

        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(bluetoothReceiver, bluetoothFilter);

        bluetoothAdapter.startDiscovery();
    }

    private void connectDevice(String zowiAddress) {
        BluetoothDevice zowiDevice = bluetoothAdapter.getRemoteDevice(zowiAddress);
        try {
            BluetoothSocket bluetoothSocket = zowiDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            Log.i("connectDevice", "bluetoothSocket conectado");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toFreeGame(View v) {

    }

    public void toGuidedGame(View v) {
        Intent intent = new Intent(getApplicationContext(), GuidedGameActivity.class);
        startActivity(intent);
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
