package zowiapp.zowi.marco.zowiapp.zowi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class Zowi {

    private static boolean connected;
    private static String address;
    private static BluetoothDevice bluetoothDevice;

    public static boolean getConnected() {
        return connected;
    }

    static void setConnected(boolean conn) {
        connected = conn;
    }

    static void setAddress(String addr) {
        address = addr;
    }
    public static String getAddress() {
        return address;
    }

    static BluetoothDevice getBluetoothDevice(BluetoothAdapter bluetoothAdapter, String zowiAddress) {
        bluetoothDevice = bluetoothDevice != null ? bluetoothDevice : bluetoothAdapter.getRemoteDevice(zowiAddress);;

        return bluetoothDevice;
    }

}
