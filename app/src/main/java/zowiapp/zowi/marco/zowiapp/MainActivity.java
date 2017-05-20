package zowiapp.zowi.marco.zowiapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewOverlay;
import android.widget.EditText;
import android.widget.LinearLayout;

import uk.co.chrisjenx.calligraphy. CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;

public class MainActivity extends AppCompatActivity {

    private MainActivity mainActivity;
    private ProgressDialog progressDialog;
    Dialog alertDialog;

    private ZowiSocket zowiSocket;
    private static final int REQUEST_COARSE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mainActivity = this;

        showProgressDialog();
        Log.i("ConnectionStep", "ProgressDialog shown");

        zowiSocket = new ZowiSocket(this);
        zowiSocket.connectToZowi();

        displayOverlay();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    zowiSocket.startDiscovery();
                }
                break;
            default:
                break;
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
                ZowiSocket.sendCommand(input.getText().toString());
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
        if (Zowi.getConnected()) {
            Intent intent = new Intent(getApplicationContext(), GuidedGameActivity.class);
            startActivity(intent);
        }
        else {
            showAlertDialog();
        }
    }

    public void discoverZowi(View v) {
        alertDialog.cancel();
        showProgressDialog();
        zowiSocket.connectToZowi();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.DialogTheme);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.setCancelable(true);
    }

    public void closeProgressDialog() {
        progressDialog.cancel();
    }

    public void showAlertDialog() {
        alertDialog = new Dialog(mainActivity, R.style.DialogTheme);
        alertDialog.setContentView(R.layout.custom_alert_dialog);
        alertDialog.show();
    }

    public void displayOverlay() {
        final LinearLayout mainActivityContainer = (LinearLayout) findViewById(R.id.main_activity_container);
        /* The Zowi not connected overlay is shown */
        if (mainActivityContainer != null) {
            final Drawable smallZowi = Zowi.getConnected() ? ContextCompat.getDrawable(this, R.drawable.overlay_on) : ContextCompat.getDrawable(this, R.drawable.overlay_off);
            final ViewOverlay overlay = mainActivityContainer.getOverlay();

            mainActivityContainer.post(new Runnable() {
                @Override
                public void run() {
                    smallZowi.setBounds(mainActivityContainer.getWidth()-mainActivityContainer.getWidth()/CommonConstants.OVERLAY_HORIZONTAL_RATIO, mainActivityContainer.getHeight()-mainActivityContainer.getWidth()/CommonConstants.OVERLAY_HORIZONTAL_RATIO, mainActivityContainer.getWidth(), mainActivityContainer.getHeight());
                    overlay.add(smallZowi);
                }
            });
            Log.i("ConnectionStep", "Overlay shown");
        }
    }

}