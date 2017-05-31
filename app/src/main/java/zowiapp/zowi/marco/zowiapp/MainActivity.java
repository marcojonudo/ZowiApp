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

import java.util.ArrayList;
import java.util.Random;

import uk.co.chrisjenx.calligraphy. CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.utils.Layout;

public class MainActivity extends AppCompatActivity {

    private ZowiSocket zowiSocket;
    private static final int REQUEST_COARSE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Layout.drawProgressDialog(this);
        Log.i("ConnectionStep", "ProgressDialog shown");

        zowiSocket = new ZowiSocket(this);
        zowiSocket.connectToZowi();

        Layout.drawOverlay(this, findViewById(R.id.main_activity_container));
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

//    public void toFreeGame(View v) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Title");
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                ZowiSocket.sendCommand(input.getText().toString());
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }

    int generateRandomIndex(ArrayList<Integer> aList, int limit, boolean insert) {
        int n = new Random().nextInt(limit);

        n = aList.contains(n) ? generateRandomIndex(aList, limit, false) : n;
        if (insert)
            aList.add(n);

        return n;
    }

    private int[] generateComplexRandomIndex(ArrayList<ArrayList<Integer>> arrayList, int categoryLimit, int imageLimit, boolean insert) {
        int categoryIndex = new Random().nextInt(categoryLimit);
        int imageIndex = new Random().nextInt(imageLimit);

        if (arrayList.get(categoryIndex).contains(imageIndex)) {
            int[] indexes = generateComplexRandomIndex(arrayList, categoryLimit, imageLimit, false);
            categoryIndex = indexes[0];
            imageIndex = indexes[1];
        }

        if (insert)
            arrayList.get(categoryIndex).add(imageIndex);

        return new int[]{categoryIndex, imageIndex};
    }

    public void toFreeGame(View v) {
        ArrayList<ArrayList<Integer>> aList = new ArrayList<>();
        for (int i=0; i<3; i++)
            aList.add(new ArrayList<Integer>());

        generateComplexRandomIndex(aList, 3, 6, true);
        generateComplexRandomIndex(aList, 3, 6, true);
        generateComplexRandomIndex(aList, 3, 6, true);
        generateComplexRandomIndex(aList, 3, 6, true);

        int a = 0;
    }

    public void toGuidedGame(View v) {
//        if (Zowi.getConnected()) {
            Intent intent = new Intent(getApplicationContext(), GuidedGameActivity.class);
            startActivity(intent);
//        }
//        else {
//            Layout.drawAlertDialog(this);
//        }
    }

    public void discoverZowi(View v) {
        Layout.closeAlertDialog();
        Layout.drawProgressDialog(this);
        zowiSocket.connectToZowi();
    }

}