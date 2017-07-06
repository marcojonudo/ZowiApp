package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
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

import java.util.ArrayList;
import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.DragConstants;
import zowiapp.zowi.marco.zowiapp.checker.DragChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

/**
 * Created by Marco on 24/01/2017.
 */
public class DragActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private DragChecker dragChecker;
    private ImagesHandler imagesHandler;
    private String[][] dragImages;
    private String[] containerImages, texts, correction;
    private int containerElements, dragImagesNumber;
    private int[][] dragCoordinates, containerCoordinates, containerDimensions;
    private float distanceToLeft, distanceToTop;
    private int[] dragLimits, dragDimensions;

    public DragActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dragChecker = new DragChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.DRAG);

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
            dragDimensions = new int[CommonConstants.AXIS_NUMBER];
            containerCoordinates = new int[containerElements][CommonConstants.AXIS_NUMBER];
            containerDimensions = new int[containerElements][CommonConstants.AXIS_NUMBER];
            dragLimits = new int[4];

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
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        ConstraintLayout constraintImages = (ConstraintLayout) gameParameters.findViewById(R.id.constraint_images);

        if (contentContainer != null) {
            dragLimits[0] = 0;
            dragLimits[1] = 0;
            dragLimits[2] = contentContainer.getRight();
            dragLimits[3] = contentContainer.getBottom();
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }

        /* We get the coordinates and dimensions on the view and load the images in 'contentContainer' */
        /* This way, they can be dragged over the whole screen */
        if (constraintImages != null) {
            for (int i=0; i<constraintImages.getChildCount(); i++) {
                View constraintView = constraintImages.getChildAt(i);

                dragCoordinates[i][0] = (int)constraintView.getX() + constraintView.getWidth()/2;
                dragCoordinates[i][1] = (int)constraintView.getY() + constraintView.getHeight()/2;
                dragDimensions[0] = constraintView.getWidth();
                dragDimensions[1] = constraintView.getHeight();
            }

            imagesHandler.loadCategoriesImages(contentContainer, dragImages, dragImagesNumber, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, dragCoordinates, dragDimensions, correction);
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
                float viewCenterX = view.getX() + view.getWidth()/2;
                float viewCenterY = view.getY() + view.getHeight()/2;

                int index = Integer.parseInt(view.getTag().toString().split("-")[0]);

                boolean correctAnswer = false;
                int insideColumnIndex = -1;
                /* We loop trought the number of containers */
                for (int i=0; i<containerCoordinates.length; i++) {
                    if ((viewCenterX > containerCoordinates[i][0])&&(viewCenterX < (containerCoordinates[i][0]+containerDimensions[i][0]))) {
                        if (viewCenterY > containerCoordinates[i][1]) {
                            insideColumnIndex = i;

                            String imageCategory = view.getTag().toString().split("-")[1];
                            correctAnswer = dragChecker.check(gameParameters, imageCategory, correction[i]);

                            break;
                        }
                    }
                }

                if (correctAnswer) {
                    /* If the view is not completely inside the box, we move it */
                    if (view.getX() < containerCoordinates[insideColumnIndex][0]) {
                        view.setX(containerCoordinates[insideColumnIndex][0]);
                    }
                    else if ((view.getX()+view.getWidth()) > (containerCoordinates[insideColumnIndex][0]+containerDimensions[insideColumnIndex][0])) {
                        view.setX(containerCoordinates[insideColumnIndex][0]+containerDimensions[insideColumnIndex][0]-view.getWidth());
                    }

                    if (view.getY() < containerCoordinates[insideColumnIndex][1]) {
                        view.setY(containerCoordinates[insideColumnIndex][1]);
                    }
                    else if ((view.getY()+view.getHeight()) > (containerCoordinates[insideColumnIndex][1]+containerDimensions[insideColumnIndex][1])) {
                        view.setY(containerCoordinates[insideColumnIndex][1]+containerDimensions[insideColumnIndex][1]-view.getHeight());
                    }
                }
                else {
                    Animations.translateAnimation(view, dragCoordinates, index);
                }
                break;
            default:
                break;
        }
    }

}
