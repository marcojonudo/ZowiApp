package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
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
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class FoodPyramidActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private FoodPyramidChecker foodPyramidChecker;
    private String[][] images;
    private int imagesNumber;
    private String[] correction;
    private int[][] imagesCoordinates, pyramidLimitsCoordinates;
    private float startX, startY, upperLimit = 0;

    public FoodPyramidActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        foodPyramidChecker = new FoodPyramidChecker();

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(FoodPyramidConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonCorrection = activityDetails.getJSONArray(FoodPyramidConstants.JSON_PARAMETER_CORRECTION);
            images = new String[jsonImages.length()][];
            imagesNumber = activityDetails.getInt(FoodPyramidConstants.JSON_PARAMETER_IMAGESNUMBER);
            correction = new String[jsonCorrection.length()];

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
        LinearLayout foodPyramidActivityTemplate = (LinearLayout) inflater.inflate(R.layout.foodpyramid_activity_template, contentContainer, false);

        if (contentContainer != null) {
            contentContainer.addView(foodPyramidActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(FoodPyramidConstants.FOODPYRAMID_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        FrameLayout mainImageContainer = (FrameLayout) gameParameters.findViewById(R.id.main_image_container);

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

            FrameLayout layoutBehindImages = (FrameLayout) gameParameters.findViewById(R.id.layout_behind_images);

            if (layoutBehindImages != null) {
                float imageSide = gameParameters.getResources().getDimension(R.dimen.food_image_side);

                /* Limits for the images to fit the screen */
                int leftLimit = layoutBehindImages.getLeft();
                int rightLimit = layoutBehindImages.getLeft() + layoutBehindImages.getWidth() - (int)imageSide;
                int upperLimit = layoutBehindImages.getTop();
                int bottomLimit = layoutBehindImages.getTop() + layoutBehindImages.getHeight() - (int)imageSide;

                int[] limits = {leftLimit, rightLimit, upperLimit, bottomLimit};
                imagesCoordinates = new int[imagesNumber][CommonConstants.AXIS_NUMBER];

                for (int i=0; i<imagesNumber; i++) {
                    int x = new Random().nextInt((limits[1] - limits[0]) + 1) + limits[0];
                    int y = new Random().nextInt((limits[3] - limits[2]) + 1) + limits[2];
                    imagesCoordinates[i][0] = x;
                    imagesCoordinates[i][1] = y;
                }

                placeImages(contentContainer, images);
            }
        }
    }

    private void placeImages(RelativeLayout contentContainer, String[][] images) {
        int[][] occurrences = new int[images.length][];
        int[] categoryOccurrences = new int[images.length];
        for (int i=0; i<occurrences.length; i++) {
            occurrences[i] = new int[images[i].length];
            categoryOccurrences[i] = 0;
            for (int j=0; j<occurrences[i].length; j++) {
                occurrences[i][j] = 0;
            }
        }

        /* We ensure that at least one image of each category is displayed */
        for (int i=0; i<images.length; i++) {
            int randomImagesIndex = new Random().nextInt(images.length);
            int randomCategoryIndex = new Random().nextInt(images[0].length);

            while (categoryOccurrences[randomImagesIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
            }
            categoryOccurrences[randomImagesIndex] = 1;
            occurrences[randomImagesIndex][randomCategoryIndex] = 1;

            occurrences[randomImagesIndex][randomCategoryIndex] = 1;
            placeImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i, correction[randomImagesIndex]);
        }

        /* randomImagesIndex is always 0 because we only want two images of the first category (base of the pyramid) */
        for (int i=images.length; i<imagesNumber; i++) {
            int randomImagesIndex = 0;
            /* images index can be 0, as all the subarrays have always the same number of images */
            int randomCategoryIndex = new Random().nextInt(images[0].length);

            while (occurrences[randomImagesIndex][randomCategoryIndex] == 1) {
                randomImagesIndex = 0;
                randomCategoryIndex = new Random().nextInt(images[0].length);
            }
            occurrences[randomImagesIndex][randomCategoryIndex] = 1;

            placeImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i, correction[randomImagesIndex]);
        }
    }

    private void placeImage(RelativeLayout container, String imageName, int i, String correction) {
        int side = (int)gameParameters.getResources().getDimension(R.dimen.food_image_side);

        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(side, side);
        image.setLayoutParams(layoutParams);
        image.setX(imagesCoordinates[i][0]);
        image.setY(imagesCoordinates[i][1]);
        String tag = i + "-" + correction;
        image.setTag(tag);

        container.addView(image);

        TouchListener touchListener = new TouchListener(FoodPyramidConstants.FOODPYRAMID_TYPE, this);
        image.setOnTouchListener(touchListener);
    }

    protected void processTouchEvent(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /* Values used to calculate de distance to move the element */
                startX = event.getRawX();
                startY = event.getRawY();

                view.bringToFront();
                break;
            case MotionEvent.ACTION_MOVE:
                /* The distance of the element to the start point is calculated when the user
                   moves it */
                float distanceX = event.getRawX() - startX;
                float distanceY = event.getRawY() - startY;

                int index = Integer.parseInt(view.getTag().toString().split("-")[0]);

                /* Mechanism to avoid the element to move behind the title and description
                   It is only moved when it is in 'contentContainer' */
                if (event.getRawY() > upperLimit) {
                    view.setX(imagesCoordinates[index][0]+distanceX);
                    view.setY(imagesCoordinates[index][1]+distanceY);

                    if (view.getY()<=0) {
                        upperLimit = event.getRawY();
                    }
                }
                else {
                    view.setX(imagesCoordinates[index][0]+distanceX);
                }
                break;
            case MotionEvent.ACTION_UP:
                float viewX = view.getX() + view.getWidth()/2;
                float viewY = view.getY() + view.getHeight()/2;

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
                    String imageCategory = view.getTag().toString().split("-")[1];
                    foodPyramidChecker.check(gameParameters, imageCategory, correction[step]);
                }
                break;
            default:
                break;
        }
    }

}
