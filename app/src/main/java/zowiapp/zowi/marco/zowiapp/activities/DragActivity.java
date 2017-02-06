package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
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
    private String[][] dragImages;
    private String[] containerImages, texts;
    private int containerElements, dragImagesNumber;
    private int[][] dragCoordinates, containerCoordinates;
    float startX, startY, upperLimit = 0;

    public DragActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
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
            JSONArray jsonDragImages = activityDetails.getJSONArray(DragConstants.JSON_PARAMETER_DRAGIMAGES);
            containerElements = activityDetails.getInt(DragConstants.JSON_PARAMETER_CONTAINERELEMENTS);
            dragImagesNumber = activityDetails.getInt(DragConstants.JSON_PARAMETER_DRAGIMAGESNUMBER);
            JSONArray jsonContainerImages = activityDetails.getJSONArray(DragConstants.JSON_PARAMETER_CONTAINERIMAGES);
            JSONArray jsonTexts = activityDetails.getJSONArray(DragConstants.JSON_PARAMETER_TEXTS);
            dragImages = new String[jsonDragImages.length()][];
            containerImages = new String[jsonContainerImages.length()];
            texts = new String[jsonTexts.length()];
            dragCoordinates = new int[dragImagesNumber][CommonConstants.AXIS_NUMBER];
            containerCoordinates = new int[containerElements][CommonConstants.AXIS_NUMBER];

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
        RelativeLayout dragActivityTemplate = (RelativeLayout) inflater.inflate(R.layout.drag_activity_template, contentContainer, false);

        RelativeLayout gridContainer = (RelativeLayout) dragActivityTemplate.findViewById(R.id.grid_container);

        LinearLayout gridTemplate = null;
        switch (containerElements) {
            case 3:
                gridTemplate = (LinearLayout) inflater.inflate(R.layout.grid_1x3_template, gridContainer, false);
                break;
            case 4:
                gridTemplate = (LinearLayout) inflater.inflate(R.layout.grid_1x4_template, gridContainer, false);
                break;
            case 5:
                gridTemplate = (LinearLayout) inflater.inflate(R.layout.grid_1x5_template, gridContainer, false);
                break;
            default:
                break;
        }

        if (gridTemplate != null) {
            /* Depending on the activity, there can be images, texts or both */
            for (int i=0; i<containerImages.length; i++) {
                LinearLayout element = (LinearLayout) gridTemplate.getChildAt(i);
                ImageView containerImage = (ImageView) element.getChildAt(1);
                containerImage.setImageResource(gameParameters.getResources().getIdentifier(containerImages[i], "drawable", gameParameters.getPackageName()));
            }
            for (int i=0; i<texts.length; i++) {
                LinearLayout element = (LinearLayout) gridTemplate.getChildAt(i);
                TextView containerText = (TextView) element.getChildAt(0);
                containerText.setText(texts[i]);
            }

            gridContainer.addView(gridTemplate);
        }

        if (contentContainer != null) {
            contentContainer.addView(dragActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(DragConstants.DRAG_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        RelativeLayout gridContainer = (RelativeLayout) gameParameters.findViewById(R.id.grid_container);
        LinearLayout cellsContainer = (LinearLayout) gameParameters.findViewById(R.id.cells_container);

        if ((gridContainer != null)&&(contentContainer != null)) {
            if (cellsContainer != null) {
                for (int i=0; i<containerElements; i++) {
                    LinearLayout element = (LinearLayout) cellsContainer.getChildAt(i);
                    ImageView containerImage = (ImageView) element.getChildAt(1);

                    /* As 'gridContainer' and 'cellsContainer' fill the whole width of 'contentContainer',
                    the method getLeft() should return 0 in these cases */
                    int x = gridContainer.getLeft() + cellsContainer.getLeft() + element.getLeft() + containerImage.getLeft();
                    /* The center of the cell is stored, instead of the upper left corner */
                    x += containerImage.getWidth()/2;

                    /* In this case, getTop() from 'cellsContainer' and 'element' should be 0 */
                    int y = gridContainer.getTop() + cellsContainer.getTop() + element.getTop() + containerImage.getTop();
                    y += containerImage.getHeight()/2;

                    containerCoordinates[i][0] = x;
                    containerCoordinates[i][1] = y;
                }
            }

            int heightCenterImage = gridContainer.getTop() / 2;
        /* Dividing between containerElements+1, we obtain the distance from the left of the first
           vertical line where an element will be placed */
            int baseWidth = contentContainer.getWidth() / (dragImagesNumber+1);

        /* As now we have more images, some of which will be selected randomly, the limit of this loop must
           be the variable with the total amount of drag images */
            for (int i=0; i<dragImagesNumber; i++) {
                dragCoordinates[i][0] = baseWidth + (baseWidth*i) - DragConstants.DRAG_IMAGE_WIDTH_PX/2;
                dragCoordinates[i][1] = heightCenterImage - DragConstants.DRAG_IMAGE_WIDTH_PX/2;
            }
        }

        placeImages(contentContainer, dragImages);
    }

    private void placeImages(RelativeLayout contentContainer, String[][] images) {
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
            placeImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i);
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

            placeImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i);
        }
    }

    private void placeImage(RelativeLayout container, String imageName, int i) {
        int side = (int) gameParameters.getResources().getDimension(R.dimen.drag_image_side);
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(side, side);
        image.setLayoutParams(layoutParams);
        image.setX(dragCoordinates[i][0]);
        image.setY(dragCoordinates[i][1]);
        image.setTag(i);

        container.addView(image);

        TouchListener touchListener = new TouchListener(DragConstants.DRAG_TYPE, this);
        image.setOnTouchListener(touchListener);
    }

    protected void processTouchEvent(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /* Values used to calculate de distance to move the element */
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                /* The distance of the element to the start point is calculated when the user
                   moves it */
                float distanceX = event.getRawX() - startX;
                float distanceY = event.getRawY() - startY;

                /* Mechanism to avoid the element to move behind the title and description
                   It is only moved when it is in 'contentContainer' */
                if (event.getRawY() > upperLimit) {
                    view.setX(dragCoordinates[(int)view.getTag()][0]+distanceX);
                    view.setY(dragCoordinates[(int)view.getTag()][1]+distanceY);

                    if (view.getY()<=0) {
                        upperLimit = event.getRawY();
                    }
                }
                else {
                    view.setX(dragCoordinates[(int)view.getTag()][0]+distanceX);
                }
                break;
            case MotionEvent.ACTION_UP:
                float viewX = view.getX() + view.getWidth()/2;
                float viewY = view.getY() + view.getHeight()/2;

                for (int i=0; i<containerElements; i++) {
                    double distanceToPoint = Math.sqrt(Math.pow(viewX-containerCoordinates[i][0], 2) + Math.pow(viewY-containerCoordinates[i][1], 2));

                    if (distanceToPoint < DragConstants.DISTANCE_LIMIT) {
                        view.setX(containerCoordinates[i][0]-DragConstants.DRAG_IMAGE_WIDTH_PX/2);
                        view.setY(containerCoordinates[i][1]-DragConstants.DRAG_IMAGE_WIDTH_PX/2);
                        break;
                    }
                }
                break;
            default:
                break;
        }
    }

}
