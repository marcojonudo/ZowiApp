package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.ImageView;
import zowiapp.zowi.marco.zowiapp.ZowiSocket;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.FoodPyramidActivity;
import zowiapp.zowi.marco.zowiapp.utils.Animations;

/**
 * Created by Marco on 03/02/2017.
 */
public class FoodPyramidChecker extends CheckerTemplate {

    private boolean checkAnswers, killThread;
    private int state;
    private static final int WAITING_ZOWI_CHECKS = 0;
    private static final int ZOWI_HAS_CHECKED = 1;

    public FoodPyramidChecker() {
        checkAnswers = false;
        killThread = false;
        state = 0;
    }

    public boolean check(GameParameters gameParameters, String[][] correctionArray, ImageView[] imageViews, int[][] imagesCoordinates) {
        sendDataToZowi(ZOWI_CHECKS_ANSWERS);

        new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !killThread) {
                    int bytesAvailable = ZowiSocket.isInputStreamAvailable();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[64];
                        ZowiSocket.readInputStream(packetBytes);

                        String receivedText = new String(packetBytes, 0, bytesAvailable);
                        /* sendFinalAck from Zowi sends an 'F' as response to ZOWI_CHECKS_ANSWERS */
                        if (receivedText.contains("F") && state == WAITING_ZOWI_CHECKS) {
                            checkAnswers = true;
                            state = ZOWI_HAS_CHECKED;
                        }
                        else if (receivedText.contains("F") && state == ZOWI_HAS_CHECKED) {
                            killThread = true;
                            state = WAITING_ZOWI_CHECKS;
                        }

                    }
                }
                checkAnswers = false;
                killThread = false;
            }
        }).start();

        while (!checkAnswers) {}

        int correctAnswers = 0;
        for (String[] correctionStep: correctionArray) {
            if (!correctionStep[0].equals(correctionStep[1])) {
                for (int i=0; i<imageViews.length; i++) {
                    if (!correctionArray[i][0].equals(correctionArray[i][1])) {
                        Animations.translateAnimation(imageViews[i], imagesCoordinates, i);
                    }
                    else
                        correctAnswers++;
                }
                FoodPyramidActivity.setImagesCounter(correctAnswers);
                sendDataToZowi(WRONG_ANSWER_COMMAND);

                return false;
            }
        }
        sendDataToZowi(CORRECT_ANSWER_COMMAND);
        return true;
    }

}
