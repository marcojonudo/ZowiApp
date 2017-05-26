package zowiapp.zowi.marco.zowiapp.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewOverlay;

import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.Zowi;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants;

/**
 * Created by Marco on 26/05/2017.
 */

public class Layout {

    private static ProgressDialog progressDialog;
    private static Dialog alertDialog;

    public static void drawOverlay(Context context, final View guidedGameScroller) {
        final Drawable smallZowi = Zowi.getConnected() ? ContextCompat.getDrawable(context, R.drawable.overlay_on) : ContextCompat.getDrawable(context, R.drawable.overlay_off);
        final ViewOverlay overlay = guidedGameScroller.getOverlay();

        guidedGameScroller.post(new Runnable() {
            @Override
            public void run() {
                smallZowi.setBounds(guidedGameScroller.getWidth()-guidedGameScroller.getWidth()/ ActivityConstants.CommonConstants.OVERLAY_HORIZONTAL_RATIO, guidedGameScroller.getHeight()-guidedGameScroller.getWidth()/ ActivityConstants.CommonConstants.OVERLAY_HORIZONTAL_RATIO, guidedGameScroller.getWidth(), guidedGameScroller.getHeight());
                overlay.add(smallZowi);
            }
        });
    }

    public static void drawProgressDialog(Context context) {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(context, R.style.DialogTheme);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.setCancelable(true);
    }

    public static void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.cancel();
    }

    public static void drawAlertDialog(Context context) {
        if (alertDialog == null)
            alertDialog = new Dialog(context, R.style.DialogTheme);
        alertDialog.setContentView(R.layout.custom_alert_dialog);
        alertDialog.show();
    }

    public static void closeAlertDialog() {
        if (alertDialog != null)
            alertDialog.cancel();
    }
}
