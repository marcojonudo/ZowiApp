package zowiapp.zowi.marco.zowiapp.checker;

import android.graphics.Point;
import android.widget.ImageView;

import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.FoodPyramidActivity;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler.ThreadType;

public class FoodPyramidChecker extends CheckerTemplate {

    private static final int WAITING_ZOWI_CHECKS = 0;
    private static final int ZOWI_HAS_CHECKED = 1;

    public FoodPyramidChecker() {}

    public boolean check(GameParameters gameParameters, String[][] correctionArray, ImageView[] imageViews, Point[] imagesCoordinates) {
        /* The thread to handle the first iteration with Zowi (when he sees the screen and tells the app to check the answers) is created */
        Thread zowiSeeScreenThread = ThreadHandler.createThread(ThreadType.SIMPLE_FEEDBACK);
        zowiSeeScreenThread.start();

        sendDataToZowi(ZowiActions.ZOWI_CHECKS_ANSWERS);

        try {
            zowiSeeScreenThread.join();

            int correctAnswers = 0;
            for (String[] correctionStep: correctionArray) {
                if (!correctionStep[0].equals(correctionStep[1])) {
                    for (int i=0; i<imageViews.length; i++) {
                        if (!correctionArray[i][0].equals(correctionArray[i][1]))
                            Animations.translateAnimation(imageViews[i], imagesCoordinates, i);
                        else
                            correctAnswers++;
                    }
                    FoodPyramidActivity.setImagesCounter(correctAnswers);
                    sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);

                    return false;
                }
            }
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        new Thread(new Runnable() {
//            public void run() {
//                while (!Thread.currentThread().isInterrupted() && !killThread) {
//                    int bytesAvailable = ZowiSocket.isInputStreamAvailable();
//                    if (bytesAvailable > 0) {
//                        byte[] packetBytes = new byte[64];
//                        ZowiSocket.readInputStream(packetBytes);
//
//                        String receivedText = new String(packetBytes, 0, bytesAvailable);
//                        /* sendFinalAck from Zowi sends an 'F' as response to ZOWI_CHECKS_ANSWERS */
//                        if (receivedText.contains("F") && state == WAITING_ZOWI_CHECKS) {
//                            checkAnswers = true;
//                            state = ZOWI_HAS_CHECKED;
//                        }
//                        else if (receivedText.contains("F") && state == ZOWI_HAS_CHECKED) {
//                            killThread = true;
//                            state = WAITING_ZOWI_CHECKS;
//                        }
//
//                    }
//                }
//                checkAnswers = false;
//                killThread = false;
//            }
//        }).start();
//
//        while (!checkAnswers) {}
//
//        int correctAnswers = 0;
//        for (String[] correctionStep: correctionArray) {
//            if (!correctionStep[0].equals(correctionStep[1])) {
//                for (int i=0; i<imageViews.length; i++) {
//                    if (!correctionArray[i][0].equals(correctionArray[i][1]))
//                        Animations.translateAnimation(imageViews[i], imagesCoordinates, i);
//                    else
//                        correctAnswers++;
//                }
//                FoodPyramidActivity.setImagesCounter(correctAnswers);
//                sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
//
//                return false;
//            }
//        }
//        sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
//        return true;
        return false;
    }

}
