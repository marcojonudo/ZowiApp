package zowiapp.zowi.marco.zowiapp.activities;

import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.MemoryConstants;
import zowiapp.zowi.marco.zowiapp.checker.MemoryChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class MemoryActivity extends ActivityTemplate {

    int firstImageID, firstPosition, secondPosition;
    boolean first;

    public MemoryActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new MemoryChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.MEMORY);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonMemoryImages = activityDetails.getJSONArray(MemoryConstants.JSON_PARAMETER_IMAGES);
            arrayImages = new String[jsonMemoryImages.length()];
            first = true;

            for (int i = 0; i< arrayImages.length; i++) {
                arrayImages[i] = jsonMemoryImages.getString(i);
            }

            imagesHandler.init(arrayImages, null, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, null);
            generateLayout();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void generateLayout() {
        setTitleDescription(gameParameters, activityTitle, activityDescription);

        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        inflater.inflate(R.layout.guided_memory_activity_template, contentContainer, true);

        if (contentContainer != null) {
            ConstraintLayout constraintContainer = (ConstraintLayout) contentContainer.getChildAt(0);

            imagesHandler.loadMemoryImages(constraintContainer, MemoryConstants.NUMBER_OF_IMAGES, arrayImages.length, constraintContainer.getChildCount());

            new CountDownTimer(MemoryConstants.FLIP_DELAY, MemoryConstants.FLIP_DELAY) {
                public void onTick(long millisUntilFinished) {}

                public void onFinish() {
                    hideImages();
                }
            }.start();
        }

    }

    private void hideImages() {
        ConstraintLayout imagesGrid = (ConstraintLayout) gameParameters.findViewById(R.id.memory_images_container);

        if (imagesGrid != null) {
            ImageView image;
            View view;
            /* The images are flipped */
            for (int i=0; i<imagesGrid.getChildCount(); i++) {
                image = (ImageView) ((FrameLayout) imagesGrid.getChildAt(i)).getChildAt(1);
                view = ((FrameLayout) imagesGrid.getChildAt(i)).getChildAt(0);

                Animations.flip1(gameParameters, image, view);
            }
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "imagesGrid");
        }
    }

    void processTouchEvent(View view, MotionEvent event) {
        String[] eventsResult = handleEvents(ActivityType.MEMORY, view, event, null, null);
        if (eventsResult != null) {
            boolean correctAnswer = ((MemoryChecker) checker).check(gameParameters, Integer.parseInt(eventsResult[0]), Integer.parseInt(eventsResult[1]), Integer.parseInt(eventsResult[2]), Integer.parseInt(eventsResult[3]));
            first = true;
            lastImageMovement(ActivityType.MEMORY, view, null, null, -1, correctAnswer);

            checkFinishActivity(ActivityType.MEMORY, correctAnswer, MemoryConstants.NUMBER_OF_IMAGES, true);
        }
    }

}
