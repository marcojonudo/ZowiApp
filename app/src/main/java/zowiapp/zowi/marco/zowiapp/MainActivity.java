package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.chrisjenx.calligraphy. CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.utils.Layout;
import zowiapp.zowi.marco.zowiapp.zowi.Zowi;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiSocket;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {

    private ZowiSocket zowiSocket;
    private static final int REQUEST_COARSE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);

        Layout.drawProgressDialog(this);

        if (!ZowiSocket.isConnected()) {
            zowiSocket = new ZowiSocket(this);
            zowiSocket.connectToZowi();
        }

        Layout.drawOverlay(this, findViewById(R.id.main_activity_container));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (ZowiSocket.isConnected())
            ZowiSocket.closeConnection();

        if (zowiSocket.getDisconnectionReceiver() != null)
            unregisterReceiver(zowiSocket.getDisconnectionReceiver());
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        if (Zowi.getConnected()) {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            intent.putExtra("type", "FREE");
            startActivity(intent);
        }
        else {
            Layout.drawAlertDialog(this);
        }
    }

    public void toGuidedGame(View v) {
        if (Zowi.getConnected()) {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            intent.putExtra("type", "GUIDED");
            startActivity(intent);
        }
        else {
            Layout.drawAlertDialog(this);
        }
    }

    public void discoverZowi(View v) {
        Layout.closeAlertDialog();
        Layout.drawProgressDialog(this);
        zowiSocket.connectToZowi();
    }

}