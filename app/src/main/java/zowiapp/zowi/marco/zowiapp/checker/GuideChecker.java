package zowiapp.zowi.marco.zowiapp.checker;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.ZowiActions;
import zowiapp.zowi.marco.zowiapp.ZowiSocket;
import zowiapp.zowi.marco.zowiapp.activities.FoodPyramidActivity;
import zowiapp.zowi.marco.zowiapp.activities.GuideActivity;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.utils.Animations;

/**
 * Created by Marco on 03/02/2017.
 */
public class GuideChecker extends CheckerTemplate {

    GuideActivity guideActivity;
    private boolean checkAnswers, killThread;

    public GuideChecker(GuideActivity guideActivity) {
        checkAnswers = false;
        killThread = false;
        this.guideActivity = guideActivity;
    }

    public boolean check(GameParameters gameParameters, String[][] correctionArray, ImageView[] imageViews, int[][] imagesCoordinates) {
        sendDataToZowi(ZowiActions.ZOWI_CHECKS_ANSWERS);

        new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !killThread) {
                    int bytesAvailable = ZowiSocket.isInputStreamAvailable();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[64];
                        ZowiSocket.readInputStream(packetBytes);

                        String receivedText = new String(packetBytes, 0, bytesAvailable);
                        /* sendFinalAck from Zowi sends an 'F' as response to ZOWI_CHECKS_ANSWERS */
                        if (receivedText.contains("F")) {
                            checkAnswers = true;
                            killThread = true;
                        }

                    }
                }
                checkAnswers = false;
                killThread = false;
            }
        }).start();

        while (!checkAnswers) {}

        LinearLayout guideImagesContainer = (LinearLayout) gameParameters.findViewById(R.id.guide_images_container);

        if (guideImagesContainer != null) {
            ImageView imageView;
            for (int i=0; i<guideImagesContainer.getChildCount(); i++) {
                imageView = (ImageView) guideImagesContainer.getChildAt(i);
                Animations.shadeAnimation(imageView, 1.0f, 2.0f);
            }
        }
        else {
            new NullElement(gameParameters, guideActivity.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "guideImagesContainer");
        }
        return true;
    }

}
