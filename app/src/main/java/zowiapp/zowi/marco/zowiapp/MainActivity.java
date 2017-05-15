package zowiapp.zowi.marco.zowiapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOverlay;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver bluetoothReceiver;
    private static OutputStream outputStream;
    private LayoutInflater inflater;
    private MainActivity mainActivity;
    private ProgressDialog progressDialog;

    private static final int REQUEST_COARSE_LOCATION = 2;
    private static final String ZOWI_NAME = "Zowi";
    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int BOND_NONE = 10;

//    public static OutputStream getOutputStream() {
//        return outputStream;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mainActivity = this;
        this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        progressDialog = new ProgressDialog(this, R.style.DialogTheme);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.setCancelable(true);

        final LinearLayout mainActivityContainer = (LinearLayout) findViewById(R.id.main_activity_container);
        /* The Zowi not connected overlay is shown */
        if (mainActivityContainer != null) {
            final Drawable smallZowi = ContextCompat.getDrawable(this, R.drawable.overlay_off);
            final ViewOverlay overlay = mainActivityContainer.getOverlay();

            mainActivityContainer.post(new Runnable() {
                @Override
                public void run() {
                    smallZowi.setBounds(mainActivityContainer.getWidth()-mainActivityContainer.getWidth()/CommonConstants.OVERLAY_HORIZONTAL_RATIO, mainActivityContainer.getHeight()-mainActivityContainer.getWidth()/CommonConstants.OVERLAY_HORIZONTAL_RATIO, mainActivityContainer.getWidth(), mainActivityContainer.getHeight());
                    overlay.add(smallZowi);
                }
            });
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean bluetoothAvailable = checkBluetoothConnectivity(bluetoothAdapter);

        if (bluetoothAvailable) {
            checkLocationPermission();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(bluetoothReceiver);
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
            progressDialog.cancel();
            Log.i("connectDevice", "bluetoothSocket conectado");

            final LinearLayout mainActivityContainer = (LinearLayout) findViewById(R.id.main_activity_container);
            if (mainActivityContainer != null) {
                final Drawable smallZowi = ContextCompat.getDrawable(this, R.drawable.overlay_on);
                final ViewOverlay overlay = mainActivityContainer.getOverlay();

                mainActivityContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        smallZowi.setBounds(mainActivityContainer.getWidth()-mainActivityContainer.getWidth()/CommonConstants.OVERLAY_HORIZONTAL_RATIO, mainActivityContainer.getHeight()-mainActivityContainer.getWidth()/CommonConstants.OVERLAY_HORIZONTAL_RATIO, mainActivityContainer.getWidth(), mainActivityContainer.getHeight());
                        overlay.add(smallZowi);
                    }
                });
            }
        }
        catch (IOException e) {
            showAlertDialog();
            e.printStackTrace();
        }
    }

    public void toFreeGame(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendCommand(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void toGuidedGame(View v) {
        Intent intent = new Intent(getApplicationContext(), GuidedGameActivity.class);
        startActivity(intent);
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

    private void showAlertDialog() {
        Dialog alertDialog = new Dialog(mainActivity, R.style.DialogTheme);
        alertDialog.setContentView(R.layout.custom_alert_dialog);
        alertDialog.show();
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
                        showAlertDialog();
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
