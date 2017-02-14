package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.DragConstants;
import zowiapp.zowi.marco.zowiapp.checker.DragChecker;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class DragActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private DragChecker dragChecker;
    private String[][] dragImages;
    private String[] containerImages, texts, correction;
    private int containerElements, dragImagesNumber;
    private int[][] dragCoordinates, containerCoordinates, dragDimensions, containerDimensions;
    float startX, startY, upperLimit = 0;

    public DragActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dragChecker = new DragChecker();

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonDragImages = activityDetails.getJSONArray(DragConstants.JSON_PARAMETER_DRAGIMAGES);
            containerElements = activityDetails.getInt(DragConstants.JSON_PARAMETER_CONTAINERELEMENTS);
            dragImagesNumber = activityDetails.getInt(DragConstants.JSON_PARAMETER_DRAGIMAGESNUMBER);
            JSONArray jsonContainerImages = activityDetails.getJSONArray(DragConstants.JSON_PARAMETER_CONTAINERIMAGES);
            JSONArray jsonTexts = activityDetails.getJSONArray(DragConstants.JSON_PARAMETER_TEXTS);
            JSONArray jsonCorrection = activityDetails.getJSONArray(DragConstants.JSON_PARAMETER_CORRECTION);
            dragImages = new String[jsonDragImages.length()][];
            containerImages = new String[jsonContainerImages.length()];
            texts = new String[jsonTexts.length()];
            correction = new String[jsonCorrection.length()];
            dragCoordinates = new int[dragImagesNumber][CommonConstants.AXIS_NUMBER];
            dragDimensions = new int[dragImagesNumber][CommonConstants.AXIS_NUMBER];
            containerCoordinates = new int[containerElements][CommonConstants.AXIS_NUMBER];
            containerDimensions = new int[containerElements][CommonConstants.AXIS_NUMBER];

            /* Drag elements number doesn't have to be the same as container elements one */
            for (int i=0; i<dragImages.length; i++) {
                JSONArray jsonCategoryImages = jsonDragImages.getJSONArray(i);
                dragImages[i] = new String[jsonCategoryImages.length()];
                for (int j=0; j<dragImages[i].length; j++) {
                    dragImages[i][j] = jsonCategoryImages.getString(j);
                }
            }
            for (int i=0; i<containerImages.length; i++) {
                containerImages[i] = jsonContainerImages.getString(i);
            }
            for (int i=0; i<texts.length; i++) {
                texts[i] = jsonTexts.getString(i);
            }
            for (int i=0; i<correction.length; i++) {
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

        ConstraintLayout dragActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.drag_activity_template, contentContainer, false);
        ConstraintLayout constraintContainer = (ConstraintLayout) dragActivityTemplate.findViewById(R.id.constraint_container);

        Guideline guideline = (Guideline) dragActivityTemplate.findViewById(R.id.drag_guideline);

        /* Generation of the drag images layout */
        switch (dragImagesNumber) {
            case 5:
                /* It is not necessary to set the constraint relations dynamically. They can be defined in the xml,
                   so we can directly inflate the container */
                inflater.inflate(R.layout.drag_images_1x5_template, constraintContainer, true);
                break;
            case 7:
                inflater.inflate(R.layout.drag_images_1x7_template, constraintContainer, true);
                break;
            default:
                break;
        }

        /* Generation of the containers layout */
        switch (containerElements) {
            case 3:
                inflater.inflate(R.layout.drag_container_1x3_template, constraintContainer, true);
                break;
            case 5:
                inflater.inflate(R.layout.drag_container_1x5_template, constraintContainer, true);
                break;
            default:
                break;
        }

        if (contentContainer != null) {
            contentContainer.addView(dragActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(DragConstants.DRAG_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        ConstraintLayout constraintImages = (ConstraintLayout) gameParameters.findViewById(R.id.constraint_images);

        /* We get the coordinates and dimensions on the view and load the images in 'contentContainer' */
        /* This way, they can be dragged over the whole screen */
        if (constraintImages != null) {
            for (int i=0; i<constraintImages.getChildCount(); i++) {
                View constraintView = constraintImages.getChildAt(i);

                dragCoordinates[i][0] = (int)constraintView.getX() + constraintView.getWidth()/2;
                dragCoordinates[i][1] = (int)constraintView.getY() + constraintView.getHeight()/2;
                dragDimensions[i][0] = constraintView.getWidth();
                dragDimensions[i][1] = constraintView.getHeight();
            }

            loadImages(contentContainer, dragImages);
        }

        ConstraintLayout constraintImagesContainer = (ConstraintLayout) gameParameters.findViewById(R.id.constraint_images_container);

        if (constraintImagesContainer != null) {
            for (int i=0; i<constraintImagesContainer.getChildCount(); i++) {
                LinearLayout containerElement = (LinearLayout) constraintImagesContainer.getChildAt(i);

                TextView containerElementTitle = (TextView) containerElement.getChildAt(0);
                containerElementTitle.setText(texts[i]);

                View containerElementBox = containerElement.getChildAt(1);
                float x = containerElement.getLeft() + containerElementBox.getLeft() + containerElementBox.getX();
                float y = constraintImagesContainer.getTop() + containerElement.getTop() + containerElementBox.getY();
                containerCoordinates[i][0] = (int)x;
                containerCoordinates[i][1] = (int)y;
                containerDimensions[i][0] = containerElementBox.getWidth();
                containerDimensions[i][1] = containerElementBox.getHeight();
            }
        }
    }

    private void loadImages(RelativeLayout contentContainer, String[][] images) {
        /* Ocurrences array for generating random index, so the images have not the same order
           every time the child enter */
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
            loadImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i, correction[randomImagesIndex]);
        }

        /* As images are chosen randomly between all the available ones, we cannot take the length of the
           images array as reference. Instead of that, we use a number defined in activities_details */
        /* We start at 3 because indexes 0, 1 and 2 have been used just before */
        for (int i=images.length; i<dragImagesNumber; i++) {
            int randomImagesIndex = new Random().nextInt(images.length);
            /* images index can be 0, as all the subarrays have always the same number of images */
            int randomCategoryIndex = new Random().nextInt(images[0].length);

            while (occurrences[randomImagesIndex][randomCategoryIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
                randomCategoryIndex = new Random().nextInt(images[0].length);
            }
            occurrences[randomImagesIndex][randomCategoryIndex] = 1;

            loadImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i, correction[randomImagesIndex]);
        }
    }

    private void loadImage(RelativeLayout contentContainer, String imageName, int i, String correction) {
        ImageView image = (ImageView) inflater.inflate(R.layout.constraint_base_image, contentContainer, false);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));

        /* 'scaleFactor' is used to set the exact width and height to the ImageView, the same as the resource it will contain */
        Drawable drawable = image.getDrawable();
        float scaleFactor;
        if (dragDimensions[i][0] < dragDimensions[i][1]) {
            scaleFactor = (float)dragDimensions[i][0]/(float)drawable.getIntrinsicWidth();
        }
        else {
            scaleFactor = (float)dragDimensions[i][1]/(float)drawable.getIntrinsicHeight();
        }

        int width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
        int height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        image.setLayoutParams(layoutParams);
        dragCoordinates[i][0] = dragCoordinates[i][0] - width/2;
        dragCoordinates[i][1] = dragCoordinates[i][1] - height/2;
        image.setX(dragCoordinates[i][0]);
        image.setY(dragCoordinates[i][1]);
        String tag = i + "-" + correction;
        image.setTag(tag);


        TouchListener touchListener = new TouchListener(DragConstants.DRAG_TYPE, this);
        image.setOnTouchListener(touchListener);

        contentContainer.addView(image);
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

                /* The tag is the index plus the category, so it is necessary to split it */
                int index = Integer.parseInt(view.getTag().toString().split("-")[0]);

                /* Mechanism to avoid the element to move behind the title and description
                   It is only moved when it is in 'contentContainer' */
                if (event.getRawY() > upperLimit) {
                    view.setX(dragCoordinates[index][0]+distanceX);
                    view.setY(dragCoordinates[index][1]+distanceY);

                    if (view.getY()<=0) {
                        upperLimit = event.getRawY();
                    }
                }
                else {
                    view.setX(dragCoordinates[index][0]+distanceX);
                }
                break;
            case MotionEvent.ACTION_UP:
                float viewCenterX = view.getX() + view.getWidth()/2;
                float viewCenterY = view.getY() + view.getHeight()/2;

                for (int i=0; i<containerElements; i++) {
                    if ((viewCenterX > containerCoordinates[i][0])&&(viewCenterX < (containerCoordinates[i][0]+containerDimensions[i][0]))) {
                        if (viewCenterY > containerCoordinates[i][1]) {
                            /* If the view is not completely inside the box, we move it */
                            if (view.getX() < containerCoordinates[i][0]) {
                                view.setX(containerCoordinates[i][0]);
                            }
                            else if ((view.getX()+view.getWidth()) > (containerCoordinates[i][0]+containerDimensions[i][0])) {
                                view.setX(containerCoordinates[i][0]+containerDimensions[i][0]-view.getWidth());
                            }

                            if (view.getY() < containerCoordinates[i][1]) {
                                view.setY(containerCoordinates[i][1]);
                            }
                            else if ((view.getY()+view.getHeight()) > (containerCoordinates[i][1]+containerDimensions[i][1])) {
                                view.setY(containerCoordinates[i][1]+containerDimensions[i][1]);
                            }

                            String imageCategory = view.getTag().toString().split("-")[1];
                            dragChecker.check(gameParameters, imageCategory, correction[i]);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

}
