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
import android.view.View;
import android.view.ViewOverlay;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;

public class Zowi {

    private static boolean connected;

    public static boolean getConnected() {
        return connected;
    }

    public static void setConnected(boolean conn) {
        connected = conn;
    }
}
