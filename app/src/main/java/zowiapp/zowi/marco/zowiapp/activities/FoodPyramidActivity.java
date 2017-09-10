package zowiapp.zowi.marco.zowiapp.activities;

import android.graphics.Point;
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
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.FoodPyramidConstants;
import zowiapp.zowi.marco.zowiapp.checker.FoodPyramidChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class FoodPyramidActivity extends ActivityTemplate {

    static int imagesCounter;
    private Point imagesDimensions;
    ImageView[] imageViews;

    public static void setImagesCounter(int c) {
        imagesCounter = c;
    }

    public FoodPyramidActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new FoodPyramidChecker();
        //TODO Comprobar a meter imagesHandler en ActivityTemplate
        imagesHandler = new ImagesHandler(gameParameters, this, FoodPyramidConstants.FOODPYRAMID_TYPE);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(FoodPyramidConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonCorrection = activityDetails.getJSONArray(FoodPyramidConstants.JSON_PARAMETER_CORRECTION);

            doubleArrayImages = new String[jsonImages.length()][];
            correction = new String[jsonCorrection.length()];
            doubleArrayCorrection = new String[FoodPyramidConstants.NUMBER_OF_IMAGES][CommonConstants.AXIS_NUMBER];
            dragLimits = new int[CommonConstants.DRAG_LIMITS_SIZE];
            imageViews = new ImageView[FoodPyramidConstants.NUMBER_OF_IMAGES];
            containerCoordinates = Functions.createEmptyPointArray(FoodPyramidConstants.PYRAMID_COORDINATES_LENGTH);
            imagesCoordinates = Functions.createEmptyPointArray(FoodPyramidConstants.NUMBER_OF_IMAGES);
            imagesDimensions = new Point();

            /* The different types of food are stored in different indexes of 'doubleArrayImages' */
            for (int i = 0; i< doubleArrayImages.length; i++) {
                JSONArray foodTypeImages = jsonImages.getJSONArray(i);
                doubleArrayImages[i] = new String[foodTypeImages.length()];

                for (int j = 0; j< doubleArrayImages[i].length; j++) {
                    doubleArrayImages[i][j] = foodTypeImages.getString(j);
                }

                /* Correction has the same length as doubleArrayImages */
                correction[i] = jsonCorrection.getString(i);
            }

            imagesHandler.init(null, doubleArrayImages, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, correction);
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
        ConstraintLayout foodPyramidActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.guided_foodpyramid_activity_template, contentContainer, false);

        if (contentContainer != null) {
            contentContainer.addView(foodPyramidActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(FoodPyramidConstants.FOODPYRAMID_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        FrameLayout mainImageContainer = (FrameLayout) gameParameters.findViewById(R.id.main_image_container);

        if (contentContainer != null)
            setDragLimits(contentContainer);
        else
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");


        if (mainImageContainer != null) {
            /* In this case, the doubleArrayImages has been defined through xml instead of dynamically.
            It still has to be checked if the dps work as expected, and they are rescaled correctly */
            ImageView foodPyramidImage = (ImageView) gameParameters.findViewById(R.id.food_pyramid_image);

            if (foodPyramidImage != null) {
                /* We get the Y position of the top of the first step */
                int halfStepHeight = foodPyramidImage.getHeight()/(FoodPyramidConstants.PYRAMID_STEPS);
                /* As 0 is the upper, and we want to start from the base of the pyramid, we calculate
                the y coordinate first */
                int stepYCoordinate = halfStepHeight*(FoodPyramidConstants.PYRAMID_STEPS);

                int pyramidHalfWidth = foodPyramidImage.getWidth()/2;

                for (int i = 0; i< containerCoordinates.length; i++) {
                    containerCoordinates[i].set(mainImageContainer.getLeft() + foodPyramidImage.getLeft() + (pyramidHalfWidth * FoodPyramidConstants.PYRAMID_LIMITS_FACTORS[0][i]),
                            mainImageContainer.getTop() + foodPyramidImage.getTop() + stepYCoordinate-(halfStepHeight*FoodPyramidConstants.PYRAMID_LIMITS_FACTORS[1][i]));
                }
            }

            ConstraintLayout layoutBehindImages = (ConstraintLayout) gameParameters.findViewById(R.id.food_pyramid_images_container);

            if (layoutBehindImages != null) {
                View constraintView;
                /* layoutBehindImages contains 8 views for display purposes, but only 7 will be filled with an image */
                for (int i=0; i<layoutBehindImages.getChildCount()-1; i++) {
                    constraintView = layoutBehindImages.getChildAt(i);

                    imagesCoordinates[i].set(layoutBehindImages.getLeft() + (int)constraintView.getX() + constraintView.getWidth()/2,
                                                (int)constraintView.getY() + constraintView.getHeight()/2);
                    imagesDimensions.set(constraintView.getWidth(), constraintView.getHeight());
                }

                imagesHandler.loadCategoriesImages(contentContainer, FoodPyramidConstants.NUMBER_OF_IMAGES, imagesCoordinates, imagesDimensions);
            }
        }
    }

    void processTouchEvent(View view, MotionEvent event) {
        String[] eventsResult = handleEvents(ActivityType.FOODPYRAMID, view, event, null, null);
        if (eventsResult != null) {
            boolean correctAnswer = ((FoodPyramidChecker) checker).check(gameParameters, doubleArrayCorrection, imageViews, imagesCoordinates);
            lastImageMovement(ActivityType.FOODPYRAMID, view, null, null, Integer.parseInt(eventsResult[0]), correctAnswer);
        }
    }

}
