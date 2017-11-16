package zowiapp.zowi.marco.zowiapp.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameActivity;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.zowi.Zowi;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;

public class Layout {

    private static ProgressDialog progressDialog;
    private static Dialog alertDialog;

    public static void drawOverlay(Context context, final View guidedGameScroller) {
        final Drawable smallZowi = Zowi.getConnected() ? ContextCompat.getDrawable(context, R.drawable.overlay_on) : ContextCompat.getDrawable(context, R.drawable.overlay_off);
        final ViewOverlay overlay = guidedGameScroller.getOverlay();

        guidedGameScroller.post(new Runnable() {
            @Override
            public void run() {
                        smallZowi.setBounds(guidedGameScroller.getWidth()-guidedGameScroller.getWidth()/ CommonConstants.OVERLAY_HORIZONTAL_RATIO, guidedGameScroller.getHeight()-guidedGameScroller.getWidth()/ CommonConstants.OVERLAY_HORIZONTAL_RATIO, guidedGameScroller.getWidth(), guidedGameScroller.getHeight());
                overlay.add(smallZowi);
            }
        });
    }

    public static void drawProgressDialog(Context context) {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(context, R.style.DialogTheme);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.setCancelable(false);
    }

    public static void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.cancel();
    }

    public static void drawAlertDialog(Context context) {
        if (alertDialog == null)
            alertDialog = new Dialog(context, R.style.DialogTheme);
        alertDialog.setContentView(R.layout.custom_alert_dialog);
        alertDialog.setCancelable(true);
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

    public static void showAlertDialog(final GameParameters gameParameters, final boolean guidedActivity, boolean correct, String text) {
        final Dialog alertDialog = new Dialog(gameParameters, R.style.DialogTheme);
        alertDialog.setContentView(R.layout.finish_alert_dialog);

        TextView correctOperationText = (TextView) alertDialog.findViewById(R.id.correct_operation_text);
        correctOperationText.setText(text);

        ImageView finishDialogImage = (ImageView) alertDialog.findViewById(R.id.finish_dialog_image);
        finishDialogImage.setImageResource(
                gameParameters.getResources().getIdentifier(correct ? "zowi_happy_open_small" : "zowi_sad_open_small", CommonConstants.DRAWABLE, gameParameters.getPackageName()));

        alertDialog.show();

        Button restartButton = (Button) alertDialog.findViewById(R.id.restart_activity_button);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                gameParameters.recreate();
            }
        });

        Button finishButton = (Button) alertDialog.findViewById(R.id.finish_activity_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                Intent intent = new Intent(gameParameters.getApplicationContext(), GameActivity.class);
                intent.putExtra("type", guidedActivity ? "GUIDED" : "FREE");
                gameParameters.startActivity(intent);
            }
        });
    }

}
