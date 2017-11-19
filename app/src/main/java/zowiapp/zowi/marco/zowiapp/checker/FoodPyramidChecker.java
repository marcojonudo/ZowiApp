package zowiapp.zowi.marco.zowiapp.checker;

import android.graphics.Point;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.FoodPyramidConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.utils.AsyncTaskHandler;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.FoodPyramidActivity;
import zowiapp.zowi.marco.zowiapp.utils.Animations;

public class FoodPyramidChecker extends CheckerTemplate {

    private FoodPyramidActivity foodPyramidActivity;
    private int totalCorrectAnswers = 0;
    private ArrayList<String[]> dynamicCorrection;
    private ArrayList<Point> dynamicCoordinates;
    private ArrayList<ImageView> imageViews;
    private Point[] imagesCoordinates;

    public FoodPyramidChecker() {}

    public void check(FoodPyramidActivity foodPyramidActivity, ArrayList<String[]> dynamicCorrection, ArrayList<ImageView> imageViews, ArrayList<Point> dynamicCoordinates) {
        this.foodPyramidActivity = foodPyramidActivity;
        this.dynamicCorrection = dynamicCorrection;
        this.imageViews = imageViews;
        this.dynamicCoordinates = dynamicCoordinates;
        this.imagesCoordinates = new Point[dynamicCoordinates.size()];
        for (int i=0; i<dynamicCoordinates.size(); i++)
            this.imagesCoordinates[i] = dynamicCoordinates.get(i);
        dynamicCoordinates.clear();

        /* The thread to handle the first iteration with Zowi (when he sees the screen and tells the app to check the answers) is created */
        new AsyncTaskHandler(this, ActivityType.FOODPYRAMID).execute(ZowiActions.ZOWI_CHECKS_ANSWERS);
    }

    public void checkAnswers() {
        boolean correctAnswer = true;
        for (int i=0; i<imageViews.size(); i++) {
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
            totalCorrectAnswers = 0;
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            foodPyramidActivity.registerCorrectAnswer();
        }

        if (!correctAnswer)
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);

        resetValues();
    }

    private void resetValues() {
        imageViews.clear();
        dynamicCorrection.clear();
        dynamicCoordinates.clear();
    }

}
