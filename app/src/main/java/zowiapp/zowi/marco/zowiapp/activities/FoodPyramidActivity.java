package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.FoodPyramidConstants;
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
    private String[][] images;
    private int[][] pyramidCoordinates, imagesCoordinates;
    float startX, startY, upperLimit = 0;

    public FoodPyramidActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(FoodPyramidConstants.JSON_PARAMETER_IMAGES);
            images = new String[jsonImages.length()][];

            /* The different types of food are stored in different indexes of 'images' */
            for (int i=0; i<images.length; i++) {
                JSONArray foodTypeImages = jsonImages.getJSONArray(i);
                images[i] = new String[foodTypeImages.length()];

                for (int j=0; j<images[i].length; j++) {
                    images[i][j] = foodTypeImages.getString(j);
                }
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

        pyramidCoordinates = new int[FoodPyramidConstants.PYRAMID_COORDINATES_LENGTH][CommonConstants.AXIS_NUMBER];

        /* In this case, the images has been defined through xml instead of dynamically.
           It still has to be checked if the dps work as expected, and they are rescaled correctly */
        ImageView foodPyramidImage = (ImageView) gameParameters.findViewById(R.id.food_pyramid_image);

        /* We get the horizontal line in the middle of the first step */
        int halfStepHeight = foodPyramidImage.getHeight()/(FoodPyramidConstants.PYRAMID_STEPS*2);
        /* As 0 is the upper, and we want to start from the base of the pyramid, we calculate
        the y coordinate first */
        int stepYCoordinate = halfStepHeight*(FoodPyramidConstants.PYRAMID_STEPS*2-1);

        int pyramidHalfWidth = foodPyramidImage.getWidth()/2;

        for (int i=0; i<pyramidCoordinates.length; i++) {
            pyramidCoordinates[i][0] = mainImageContainer.getLeft() + (int)(pyramidHalfWidth * FoodPyramidConstants.PYRAMID_X_COORDINATES_FACTORS[i]);
            pyramidCoordinates[i][1] = mainImageContainer.getTop() + stepYCoordinate-(halfStepHeight*FoodPyramidConstants.PYRAMID_Y_COORDINATES_FACTORS[i]);
        }

        FrameLayout layoutBehindImages = (FrameLayout) gameParameters.findViewById(R.id.layout_behind_images);

        float imageSide = gameParameters.getResources().getDimension(R.dimen.food_image_side);

        /* Limits for the images to fit the screen */
        int leftLimit = layoutBehindImages.getLeft();
        int rightLimit = layoutBehindImages.getLeft() + layoutBehindImages.getWidth() - (int)imageSide;
        int upperLimit = layoutBehindImages.getTop();
        int bottomLimit = layoutBehindImages.getTop() + layoutBehindImages.getHeight() - (int)imageSide;

        int[] limits = {leftLimit, rightLimit, upperLimit, bottomLimit};

        placeImages(contentContainer, images, limits);
    }

    private void placeImages(RelativeLayout contentContainer, String[][] images, int[] limits) {
        int positionIndex = 0;
        imagesCoordinates = new int[FoodPyramidConstants.IMAGES_COORDINATES_LENGTH][CommonConstants.AXIS_NUMBER];
        for (int i=0; i<images.length; i++) {
            for (int j=0; j<images[i].length; j++) {
                placeImage(contentContainer, images[i][j], limits, i, positionIndex);
                positionIndex++;
            }
        }
    }

    private void placeImage(RelativeLayout container, String imageName, int[] limits, int i, int positionIndex) {
        int x = new Random().nextInt((limits[1] - limits[0]) + 1) + limits[0];
        int y = new Random().nextInt((limits[3] - limits[2]) + 1) + limits[2];
        imagesCoordinates[positionIndex][0] = x;
        imagesCoordinates[positionIndex][1] = y;

        int side = (int)gameParameters.getResources().getDimension(R.dimen.food_image_side);

        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(side, side);
        image.setLayoutParams(layoutParams);
        image.setX(x);
        image.setY(y);
        image.setTag(i);

        container.addView(image);

        TouchListener touchListener = new TouchListener(FoodPyramidConstants.FOODPYRAMID_TYPE, this);
        image.setOnTouchListener(touchListener);
    }

    protected void processTouchEvent(View view, MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                /* Values used to calculate de distance to move the element */
//                startX = event.getRawX();
//                startY = event.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                /* The distance of the element to the start point is calculated when the user
//                   moves it */
//                float distanceX = event.getRawX() - startX;
//                float distanceY = event.getRawY() - startY;
//
//                /* Mechanism to avoid the element to move behind the title and description
//                   It is only moved when it is in 'contentContainer' */
//                if (event.getRawY() > upperLimit) {
//                    view.setX(dragCoordinates[(int)view.getTag()][0]+distanceX);
//                    view.setY(dragCoordinates[(int)view.getTag()][1]+distanceY);
//
//                    if (view.getY()<=0) {
//                        upperLimit = event.getRawY();
//                    }
//                }
//                else {
//                    view.setX(dragCoordinates[(int)view.getTag()][0]+distanceX);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                float viewX = view.getX() + view.getWidth()/2;
//                float viewY = view.getY() + view.getHeight()/2;
//
//                for (int i=0; i<containerElements; i++) {
//                    double distanceToPoint = Math.sqrt(Math.pow(viewX-containerCoordinates[i][0], 2) + Math.pow(viewY-containerCoordinates[i][1], 2));
//
//                    if (distanceToPoint < DragConstants.DISTANCE_LIMIT) {
//                        view.setX(containerCoordinates[i][0]-DragConstants.DRAG_IMAGE_WIDTH_PX/2);
//                        view.setY(containerCoordinates[i][1]-DragConstants.DRAG_IMAGE_WIDTH_PX/2);
//                        break;
//                    }
//                }
//                break;
//            default:
//                break;
//        }
    }

}
