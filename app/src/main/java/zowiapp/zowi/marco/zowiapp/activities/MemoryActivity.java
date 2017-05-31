package zowiapp.zowi.marco.zowiapp.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.MemoryConstants;
import zowiapp.zowi.marco.zowiapp.checker.MemoryChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

/**
 * Created by Marco on 24/01/2017.
 */
public class MemoryActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private String[] memoryImages;
    private int firstImageID, firstPosition;
    private boolean first;
    private MemoryChecker memoryChecker;
    private ImagesHandler imagesHandler;

    public MemoryActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        memoryChecker = new MemoryChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.MEMORY);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonMemmoryImages = activityDetails.getJSONArray(MemoryConstants.JSON_PARAMETER_IMAGES);
            memoryImages = new String[jsonMemmoryImages.length()];
            first = true;

            for (int i = 0; i< memoryImages.length; i++) {
                memoryImages[i] = jsonMemmoryImages.getString(i);
            }

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
        inflater.inflate(R.layout.memory_activity_template, contentContainer, true);

        if (contentContainer != null) {
            ConstraintLayout constraintContainer = (ConstraintLayout) contentContainer.getChildAt(0);

            imagesHandler.loadSimpleDoubleImages(constraintContainer, memoryImages, MemoryConstants.NUMBER_OF_IMAGES, memoryImages.length, constraintContainer.getChildCount());

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

    protected void processTouchEvent(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean correctAnswer = false;
                int secondPosition = -1;
                if (first) {
                    firstImageID = Integer.parseInt(view.getTag().toString().split("-")[0]);
                    firstPosition = Integer.parseInt(view.getTag().toString().split("-")[1]);
                    first = false;
                }
                else {
                    int secondImageID = Integer.parseInt(view.getTag().toString().split("-")[0]);
                    secondPosition = Integer.parseInt(view.getTag().toString().split("-")[1]);
                    correctAnswer = memoryChecker.check(gameParameters, firstImageID, secondImageID, firstPosition, secondPosition);
                    first = true;
                }

                if (correctAnswer) {
                    ConstraintLayout imagesGrid = (ConstraintLayout) gameParameters.findViewById(R.id.memory_images_container);

                    ImageView firstImage, secondImage;
                    View firstView, secondView;
                    if (imagesGrid != null) {
                        firstImage = (ImageView) ((FrameLayout) imagesGrid.getChildAt(firstPosition)).getChildAt(1);
                        firstView = ((FrameLayout) imagesGrid.getChildAt(firstPosition)).getChildAt(0);
                        secondImage = (ImageView) ((FrameLayout) imagesGrid.getChildAt(secondPosition)).getChildAt(1);
                        secondView = ((FrameLayout) imagesGrid.getChildAt(secondPosition)).getChildAt(0);

                        Animations.flip2WithDelay(gameParameters, firstImage, firstView, secondImage, secondView);
                    }
                    else {
                        new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "imagesGrid");
                    }
                }

                break;
            default:
                break;
        }
    }

}
