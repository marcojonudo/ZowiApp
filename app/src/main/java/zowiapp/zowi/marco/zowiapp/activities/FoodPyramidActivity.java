package zowiapp.zowi.marco.zowiapp.activities;

import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

/**
 * Created by Marco on 24/01/2017.
 */
public class FoodPyramidActivity extends ActivityTemplate {

    private static int imagesCounter;
    private String[][] doubleArrayCorrection;
    private String[] correction;
    private Point[] pyramidLimitsCoordinates;
    private float distanceToLeft, distanceToTop;
    private Point imagesDimensions;
    private ImageView[] imageViews;

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
            pyramidLimitsCoordinates = Functions.createEmptyPointArray(FoodPyramidConstants.PYRAMID_COORDINATES_LENGTH);
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

                for (int i=0; i<pyramidLimitsCoordinates.length; i++) {
                    pyramidLimitsCoordinates[i].set(mainImageContainer.getLeft() + foodPyramidImage.getLeft() + (pyramidHalfWidth * FoodPyramidConstants.PYRAMID_LIMITS_FACTORS[0][i]),
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

                imagesHandler.loadCategoriesImages(contentContainer, doubleArrayImages, FoodPyramidConstants.NUMBER_OF_IMAGES, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, imagesCoordinates, imagesDimensions, correction);
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
                int leftOrRight1 = (pyramidLimitsCoordinates[0].x-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1].x) *
                                        ((int)viewY-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1].y) -
                                        (pyramidLimitsCoordinates[0].y-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1].y) *
                                        ((int)viewX-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1].x);

                int leftOrRight2 = (pyramidLimitsCoordinates[1].x-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1].x) *
                                        ((int)viewY-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1].y) -
                                        (pyramidLimitsCoordinates[1].y-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1].y) *
                                        ((int)viewX-pyramidLimitsCoordinates[pyramidLimitsCoordinates.length-1].x);

                /* This equations make us know if the center of the view is above or below the base of the pyramid */
                int aboveOrBelow = (pyramidLimitsCoordinates[1].x-pyramidLimitsCoordinates[0].x) *
                                        ((int)viewY-pyramidLimitsCoordinates[0].y) -
                                        (pyramidLimitsCoordinates[1].y-pyramidLimitsCoordinates[0].y) *
                                        ((int)viewX-pyramidLimitsCoordinates[0].x);

                /* The center of the view is inside the pyramid */
                if ((leftOrRight1 < 0)&&(leftOrRight2 > 0) && (aboveOrBelow<0)) {
                    int step;
                    if (viewY > pyramidLimitsCoordinates[2].y) {
                        step = 0;
                    }
                    else if (viewY > pyramidLimitsCoordinates[3].y) {
                        if (viewX > pyramidLimitsCoordinates[3].x) {
                            step = 1;
                        }
                        else {
                            step = 2;
                        }
                    }
                    else if (viewY > pyramidLimitsCoordinates[4].y) {
                        if (viewX > pyramidLimitsCoordinates[4].x) {
                            step = 3;
                        }
                        else {
                            step = 4;
                        }
                    }
                    else {
                        step = 5;
                    }

                    doubleArrayCorrection[imagesCounter][0] = imageCategory;
                    doubleArrayCorrection[imagesCounter][1] = correction[step];
                    imageViews[imagesCounter] = (ImageView) view;
                    imagesCounter++;
                    if (imagesCounter == FoodPyramidConstants.NUMBER_OF_IMAGES)
                        ((FoodPyramidChecker) checker).check(gameParameters, doubleArrayCorrection, imageViews, imagesCoordinates);
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
