package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.FoodPyramidConstants;
import zowiapp.zowi.marco.zowiapp.checker.FoodPyramidChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

/**
 * Created by Marco on 24/01/2017.
 */
public class FoodPyramidActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private FoodPyramidChecker foodPyramidChecker;
    private ImagesHandler imagesHandler;
    private String[][] images, correctionArray;
    private static int imagesCounter;
    private String[] correction;
    private int[][] imagesCoordinates, pyramidLimitsCoordinates;
    private float distanceToLeft, distanceToTop;
    private int[] dragLimits, imagesDimensions;
    private ImageView[] imageViews;

    public static void setImagesCounter(int c) {
        imagesCounter = c;
    }

    public FoodPyramidActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        foodPyramidChecker = new FoodPyramidChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, FoodPyramidConstants.FOODPYRAMID_TYPE);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(FoodPyramidConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonCorrection = activityDetails.getJSONArray(FoodPyramidConstants.JSON_PARAMETER_CORRECTION);
            images = new String[jsonImages.length()][];
            correction = new String[jsonCorrection.length()];
            correctionArray = new String[FoodPyramidConstants.NUMBER_OF_IMAGES][CommonConstants.AXIS_NUMBER];
            dragLimits = new int[4];
            imageViews = new ImageView[FoodPyramidConstants.NUMBER_OF_IMAGES];

            /* The different types of food are stored in different indexes of 'images' */
            for (int i=0; i<images.length; i++) {
                JSONArray foodTypeImages = jsonImages.getJSONArray(i);
                images[i] = new String[foodTypeImages.length()];

                for (int j=0; j<images[i].length; j++) {
                    images[i][j] = foodTypeImages.getString(j);
                }

                /* Correction has the same length as images */
                correction[i] = jsonCorrection.getString(i);
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
        ConstraintLayout foodPyramidActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.foodpyramid_activity_template, contentContainer, false);

        if (contentContainer != null) {
            contentContainer.addView(foodPyramidActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(FoodPyramidConstants.FOODPYRAMID_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        FrameLayout mainImageContainer = (FrameLayout) gameParameters.findViewById(R.id.main_image_container);

        if (contentContainer != null) {
            dragLimits[0] = 0;
            dragLimits[1] = 0;
            dragLimits[2] = contentContainer.getRight();
            dragLimits[3] = contentContainer.getBottom();
        }

        if (mainImageContainer != null) {
            pyramidLimitsCoordinates = new int[FoodPyramidConstants.PYRAMID_COORDINATES_LENGTH][CommonConstants.AXIS_NUMBER];
            /* In this case, the images has been defined through xml instead of dynamically.
            It still has to be checked if the dps work as expected, and they are rescaled correctly */
            ImageView foodPyramidImage = (ImageView) gameParameters.findViewById(R.id.food_pyramid_image);

            if (foodPyramidImage != null) {
                /* We get the Y position of the top of the first step */
                int halfStepHeight = foodPyramidImage.getHeight()/(FoodPyramidConstants.PYRAMID_STEPS);
                /* As 0 is the upper, and we want to start from the base of the pyramid, we calculate
                the y coordinate first */
                int stepYCoordinate = halfStepHeight*(FoodPyramidConstants.PYRAMID_STEPS);

                int pyramidHalfWidth = foodPyramidImage.getWidth()/2;

                for (int i=0; i<pyramidLimitsCoordinates.length; i++) {
                    pyramidLimitsCoordinates[i][0] = mainImageContainer.getLeft() + foodPyramidImage.getLeft() + (pyramidHalfWidth * FoodPyramidConstants.PYRAMID_LIMITS_FACTORS[0][i]);
                    pyramidLimitsCoordinates[i][1] = mainImageContainer.getTop() + foodPyramidImage.getTop() + stepYCoordinate-(halfStepHeight*FoodPyramidConstants.PYRAMID_LIMITS_FACTORS[1][i]);
                }
            }

            ConstraintLayout layoutBehindImages = (ConstraintLayout) gameParameters.findViewById(R.id.food_pyramid_images_container);

            if (layoutBehindImages != null) {
                imagesCoordinates = new int[FoodPyramidConstants.NUMBER_OF_IMAGES][CommonConstants.AXIS_NUMBER];
                imagesDimensions = new int[CommonConstants.AXIS_NUMBER];

                View constraintView;
                /* layoutBehindImages contains 8 views for display purposes, but only 7 will be filled with an image */
                for (int i=0; i<layoutBehindImages.getChildCount()-1; i++) {
                    constraintView = layoutBehindImages.getChildAt(i);

                    imagesCoordinates[i][0] = layoutBehindImages.getLeft() + (int)constraintView.getX() + constraintView.getWidth()/2;
                    imagesCoordinates[i][1] = (int)constraintView.getY() + constraintView.getHeight()/2;
                    imagesDimensions[0] = constraintView.getWidth();
                    imagesDimensions[1] = constraintView.getHeight();
                }

                imagesHandler.loadCategoriesImages(contentContainer, images, FoodPyramidConstants.NUMBER_OF_IMAGES, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, imagesCoordinates, imagesDimensions, correction);
            }
        }
    }

    void processTouchEvent(View view, MotionEvent event) {
        float left, right, top, bottom;

        LinearLayout headerText = (LinearLayout) gameParameters.findViewById(R.id.header_text);
        int headerTextHeight = 0;
        if (headerText != null) {
            headerTextHeight = headerText.getHeight();
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "headerText");
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /* Values used to calculate de distance to move the element */
                distanceToLeft = view.getX() - event.getRawX();
                distanceToTop = headerTextHeight + view.getY() - event.getRawY();

                view.bringToFront();
                break;
            case MotionEvent.ACTION_MOVE:
                left = event.getRawX() + distanceToLeft;
                right = event.getRawX() + (view.getWidth()+ distanceToLeft);
                top = event.getRawY() + distanceToTop - headerTextHeight;
                bottom = event.getRawY() + (view.getHeight()+ distanceToTop);

                /* Mechanism to avoid the element to move outside the container.
                   It is only moved when it is in 'contentContainer' */
                if ((left <= dragLimits[0] || right >= dragLimits[2])) {
                    if ((top > dragLimits[1]) && (bottom < dragLimits[3])) {
                        view.setY(top);
                    }

                }
                else if ((top <= dragLimits[1]) || (bottom >= dragLimits[3])) {
                    if ((left > dragLimits[0] && right < dragLimits[2])) {
                        view.setX(left);
                    }
                }
                else {
                    view.setX(left);
                    view.setY(top);
                }
                break;
            case MotionEvent.ACTION_UP:
                float viewX = view.getX() + view.getWidth()/2;
                float viewY = view.getY() + view.getHeight()/2;

                int index = Integer.parseInt(view.getTag().toString().split("-")[0]);
                String imageCategory = view.getTag().toString().split("-")[1];
                boolean correctAnswer = false;

                /* This equations make us know if the center of the view is between the two sides of the pyramid */
                int leftOrRight1 = (pyramidLimitsCoordinates[0][0]-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1][0]) *
                                        ((int)viewY-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1][1]) -
                                        (pyramidLimitsCoordinates[0][1]-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1][1]) *
                                        ((int)viewX-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1][0]);

                int leftOrRight2 = (pyramidLimitsCoordinates[1][0]-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1][0]) *
                                        ((int)viewY-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1][1]) -
                                        (pyramidLimitsCoordinates[1][1]-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1][1]) *
                                        ((int)viewX-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1][0]);

                /* This equations make us know if the center of the view is above or below the base of the pyramid */
                int aboveOrBelow = (pyramidLimitsCoordinates[1][0]-pyramidLimitsCoordinates[0][0]) *
                                        ((int)viewY-pyramidLimitsCoordinates[0][1]) -
                                        (pyramidLimitsCoordinates[1][1]-pyramidLimitsCoordinates[0][1]) *
                                        ((int)viewX-pyramidLimitsCoordinates[0][0]);

                /* The center of the view is inside the pyramid */
                if ((leftOrRight1 < 0)&&(leftOrRight2 > 0) && (aboveOrBelow<0)) {
                    int step;
                    if (viewY > pyramidLimitsCoordinates[2][1]) {
                        step = 0;
                    }
                    else if (viewY > pyramidLimitsCoordinates[3][1]) {
                        if (viewX > pyramidLimitsCoordinates[3][0]) {
                            step = 1;
                        }
                        else {
                            step = 2;
                        }
                    }
                    else if (viewY > pyramidLimitsCoordinates[4][1]) {
                        if (viewX > pyramidLimitsCoordinates[4][0]) {
                            step = 3;
                        }
                        else {
                            step = 4;
                        }
                    }
                    else {
                        step = 5;
                    }

                    correctionArray[imagesCounter][0] = imageCategory;
                    correctionArray[imagesCounter][1] = correction[step];
                    imageViews[imagesCounter] = (ImageView) view;
                    imagesCounter++;
                    if (imagesCounter == FoodPyramidConstants.NUMBER_OF_IMAGES)
                        foodPyramidChecker.check(gameParameters, correctionArray, imageViews, imagesCoordinates);
                }
                else {
                    Animations.translateAnimation(view, imagesCoordinates, index);
                }
                break;
            default:
                break;
        }
    }

}
