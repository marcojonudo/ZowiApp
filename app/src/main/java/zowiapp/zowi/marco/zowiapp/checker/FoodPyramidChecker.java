package zowiapp.zowi.marco.zowiapp.checker;

import android.graphics.Point;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.FoodPyramidConstants;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.FoodPyramidActivity;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler.ThreadType;

public class FoodPyramidChecker extends CheckerTemplate {

    private int totalCorrectAnswers = 0;

    public FoodPyramidChecker() {}

    public boolean check(GameParameters gameParameters, ArrayList<String[]> dynamicCorrection, ArrayList<ImageView> imageViews, Point[] imagesCoordinates) {
        /* The thread to handle the first iteration with Zowi (when he sees the screen and tells the app to check the answers) is created */
        Thread zowiSeeScreenThread = ThreadHandler.createThread(ThreadType.SIMPLE_FEEDBACK);
        zowiSeeScreenThread.start();

        sendDataToZowi(ZowiActions.ZOWI_CHECKS_ANSWERS);

        try {
            zowiSeeScreenThread.join();

            boolean correctAnswer = true;
            for (int i=0; i<imageViews.size() && correctAnswer; i++) {
                if (!dynamicCorrection.get(i)[0].equals(dynamicCorrection.get(i)[1])) {
                    Animations.translateAnimation(imageViews.get(i), imagesCoordinates, i);
                    correctAnswer = false;
                }
                else {
                    totalCorrectAnswers++;
                    imageViews.get(i).setOnTouchListener(null);
                }
            }

            if (correctAnswer && totalCorrectAnswers == FoodPyramidConstants.NUMBER_OF_IMAGES) {
                sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
        return false;
    }

}
