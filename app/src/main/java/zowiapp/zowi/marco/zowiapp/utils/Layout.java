package zowiapp.zowi.marco.zowiapp.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.Button;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.zowi.Zowi;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants;

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

    public static Button createFloatingCheckButton(GameParameters gameParameters, LayoutInflater inflater, ViewGroup contentContainer, boolean guidedActivity) {
        Point dimensions = new Point((int)gameParameters.getResources().getDimension(R.dimen.floating_check_button_width),
                (int)gameParameters.getResources().getDimension(R.dimen.floating_check_button_height));
        Point coordinates = new Point(contentContainer.getWidth() - dimensions.x,
                contentContainer.getHeight() - dimensions.y);

        Button checkButton = (Button) inflater.inflate(R.layout.floating_check_button, contentContainer, false);
        checkButton.setBackground(ContextCompat.getDrawable(gameParameters, guidedActivity ? R.drawable.floating_red_check_button : R.drawable.floating_green_check_button));

        checkButton.setWidth(dimensions.x);
        checkButton.setHeight(dimensions.y);
        checkButton.setX(coordinates.x);
        checkButton.setY(coordinates.y);

        contentContainer.addView(checkButton);

        return checkButton;
    }
}
