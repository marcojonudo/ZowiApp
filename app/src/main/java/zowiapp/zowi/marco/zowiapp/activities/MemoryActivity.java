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
import zowiapp.zowi.marco.zowiapp.error.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

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

    public MemoryActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        memoryChecker = new MemoryChecker();

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

        placeImages(contentContainer, memoryImages);

        new CountDownTimer(MemoryConstants.FLIP_DELAY, MemoryConstants.FLIP_DELAY) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                hideImages();
            }
        }.start();
    }

    private void placeImages(RelativeLayout contentContainer, String[] images) {
        ConstraintLayout constrainContainer = (ConstraintLayout) contentContainer.getChildAt(0);
        int[] occurrences = new int[images.length];
        int[] imageViewsOccurrences = new int[constrainContainer.getChildCount()];

        for (int i=0; i<occurrences.length; i++) {
            occurrences[i] = 0;
        }
        for (int i=0; i<imageViewsOccurrences.length; i++) {
            imageViewsOccurrences[i] = 0;
        }

        int randomImagesIndex, randomPositionIndex;
        ImageView imageView;
        for (int i=0; i<MemoryConstants.NUMBER_OF_IMAGES; i++) {
            randomImagesIndex = new Random().nextInt(occurrences.length);

            while (occurrences[randomImagesIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
            }
            occurrences[randomImagesIndex] = 1;

            for (int j=0; j<2; j++) {
                randomPositionIndex = new Random().nextInt(imageViewsOccurrences.length);

                while (imageViewsOccurrences[randomPositionIndex] == 1) {
                    randomPositionIndex = new Random().nextInt(imageViewsOccurrences.length);
                }
                imageViewsOccurrences[randomPositionIndex] = 1;

                imageView = (ImageView) ((FrameLayout) constrainContainer.getChildAt(randomPositionIndex)).getChildAt(1);
                placeImage(imageView, images[randomImagesIndex], randomImagesIndex, randomPositionIndex);
            }
        }
    }

    private void placeImage(ImageView imageView, String imageName, int randomImagesIndex, int randomPositionIndex) {
        imageView.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        imageView.setTag(randomImagesIndex + "-" + randomPositionIndex);

        TouchListener touchListener = new TouchListener(MemoryConstants.MEMORY_TYPE, this);
        imageView.setOnTouchListener(touchListener);
    }

    private void hideImages() {
        ConstraintLayout imagesGrid = (ConstraintLayout) gameParameters.findViewById(R.id.memory_images_container);

        if (imagesGrid != null) {
            Animator setRightOut, setLeftIn;
            ImageView image;
            View view;
            /* The images are flipped */
            for (int i=0; i<imagesGrid.getChildCount(); i++) {
                setRightOut = AnimatorInflater.loadAnimator(gameParameters, R.animator.card_flip_right_out);
                setLeftIn = AnimatorInflater.loadAnimator(gameParameters, R.animator.card_flip_left_in);
                image = (ImageView) ((FrameLayout) imagesGrid.getChildAt(i)).getChildAt(1);
                view = ((FrameLayout) imagesGrid.getChildAt(i)).getChildAt(0);

                setRightOut.setTarget(image);
                setLeftIn.setTarget(view);
                setRightOut.start();
                setLeftIn.start();
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

                    Animator setLeftOut, setRightIn;
                    ImageView firstImage, secondImage;
                    View firstView, secondView;
                    if (imagesGrid != null) {
                        setLeftOut = AnimatorInflater.loadAnimator(gameParameters, R.animator.card_flip_right_in);
                        setRightIn = AnimatorInflater.loadAnimator(gameParameters, R.animator.card_flip_left_out);
                        firstImage = (ImageView) ((FrameLayout) imagesGrid.getChildAt(firstPosition)).getChildAt(1);
                        firstView = ((FrameLayout) imagesGrid.getChildAt(firstPosition)).getChildAt(0);

                        setLeftOut.setTarget(firstImage);
                        setRightIn.setTarget(firstView);
                        setLeftOut.start();
                        setRightIn.start();

                        setLeftOut = AnimatorInflater.loadAnimator(gameParameters, R.animator.card_flip_right_in);
                        setRightIn = AnimatorInflater.loadAnimator(gameParameters, R.animator.card_flip_left_out);
                        secondImage = (ImageView) ((FrameLayout) imagesGrid.getChildAt(secondPosition)).getChildAt(1);
                        secondView = ((FrameLayout) imagesGrid.getChildAt(secondPosition)).getChildAt(0);

                        setLeftOut.setTarget(secondImage);
                        setRightIn.setTarget(secondView);
                        setLeftOut.start();
                        setRightIn.start();
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
