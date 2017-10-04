package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ColumnsConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.checker.ColumnsChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class ColumnsActivity extends ActivityTemplate {

    private String leftColumnTitle, rightColumnTitle;
    private Point[] columnsDimensions;
    private Point imagesDimensions;

    public ColumnsActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new ColumnsChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.COLUMNS);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            leftColumnTitle = activityDetails.getString(ColumnsConstants.JSON_PARAMETER_LEFTTITLE);
            rightColumnTitle = activityDetails.getString(ColumnsConstants.JSON_PARAMETER_RIGHTTITLE);
            JSONArray jsonImages = activityDetails.getJSONArray(ColumnsConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonCorrection = activityDetails.getJSONArray(ColumnsConstants.JSON_PARAMETER_CORRECTION);
            doubleArrayImages = new String[jsonImages.length()][];
            correction = new String[jsonCorrection.length()];
            dragLimits = new int[CommonConstants.DRAG_LIMITS_SIZE];
            imagesCoordinates = Functions.createEmptyPointArray(ColumnsConstants.NUMBER_OF_IMAGES);
            /* containerCoordinates will contain the coordinates of the corners of the columns */
            containerCoordinates = Functions.createEmptyPointArray(ColumnsConstants.NUMBER_OF_COLUMNS_CORNERS);
            columnsDimensions = Functions.createEmptyPointArray(ColumnsConstants.NUMBER_OF_COLUMNS_CORNERS);
            imagesDimensions = new Point();

            for (int i = 0; i< doubleArrayImages.length; i++) {
                JSONArray jsonCategoryImages = jsonImages.getJSONArray(i);
                doubleArrayImages[i] = new String[jsonCategoryImages.length()];
                for (int j = 0; j< doubleArrayImages[i].length; j++) {
                    doubleArrayImages[i][j] = jsonCategoryImages.getString(j);
                }
            }
            for (int i=0; i<jsonCorrection.length(); i++) {
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
        ConstraintLayout columnsActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.guided_columns_activity_template, contentContainer, false);

        TextView leftTitle = (TextView) columnsActivityTemplate.findViewById(R.id.left_title);
        TextView rightTitle = (TextView) columnsActivityTemplate.findViewById(R.id.right_title);
        leftTitle.setText(leftColumnTitle);
        rightTitle.setText(rightColumnTitle);

        if (contentContainer != null) {
            contentContainer.addView(columnsActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(ColumnsConstants.COLUMNS_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        ConstraintLayout grid = (ConstraintLayout) gameParameters.findViewById(R.id.columns_grid);

        if (contentContainer != null)
            setDragLimits(contentContainer);
        else
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");

        if (grid != null) {
            int left = grid.getLeft();
            int top = grid.getTop();

            /* Cell center coordinates are calculated based on the upper left corner of the grid */
            int index = 0;
            for (int i=0; i<grid.getChildCount(); i++) {
                /* We only store even coordinates (cells 0, 2, 4...) */
                if (i%2 == 0) {
                    View cell = grid.getChildAt(i);
                    imagesCoordinates[index].set(left + cell.getLeft() + cell.getWidth()/2, top + cell.getTop() + cell.getHeight()/2);

                    imagesDimensions.set(cell.getWidth(), cell.getHeight());

                    index++;
                }
            }

            LinearLayout leftColumnContainer = (LinearLayout) gameParameters.findViewById(R.id.left_column_container);
            LinearLayout rightColumnContainer = (LinearLayout) gameParameters.findViewById(R.id.right_column_container);
            View leftColumn = gameParameters.findViewById(R.id.left_column);
            View rightColumn = gameParameters.findViewById(R.id.right_column);

            if ((leftColumn != null)&&(rightColumn != null)&&(leftColumnContainer != null)&&(rightColumnContainer != null)) {
                /* Coordinates of the corners of the columns */

                containerCoordinates[ColumnsConstants.LEFT_COLUMN_INDEX].set(leftColumnContainer.getLeft(), leftColumn.getTop());
                containerCoordinates[ColumnsConstants.RIGHT_COLUMN_INDEX].set(rightColumnContainer.getLeft(), rightColumn.getTop());

                columnsDimensions[ColumnsConstants.LEFT_COLUMN_INDEX].set(leftColumn.getWidth(), leftColumn.getHeight());
                columnsDimensions[ColumnsConstants.RIGHT_COLUMN_INDEX].set(rightColumn.getWidth(), rightColumn.getHeight());
            }
            else {
                new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "columns");
            }

            imagesHandler.loadCategoriesImages(contentContainer, ColumnsConstants.NUMBER_OF_IMAGES, imagesCoordinates, imagesDimensions);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "grid");
        }
    }

    void processTouchEvent(View view, MotionEvent event) {
        String[] eventsResult = handleEvents(ActivityType.COLUMNS, view, event, null, columnsDimensions);
        if (eventsResult != null) {
            boolean correctAnswer = ((ColumnsChecker) checker).check(gameParameters, eventsResult[1], eventsResult[2]);
            if (correctAnswer)
                correctResults++;

            if (correctResults == ColumnsConstants.NUMBER_OF_IMAGES)
                finishActivity(ActivityType.COLUMNS, true);

            lastImageMovement(ActivityType.COLUMNS, view, null, columnsDimensions, Integer.parseInt(eventsResult[0]), correctAnswer);
        }
    }

}
