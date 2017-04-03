package zowiapp.zowi.marco.zowiapp.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ColumnsConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.checker.ColumnsChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class ColumnsActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription, leftColumnTitle, rightColumnTitle;
    private JSONObject activityDetails;
    private ColumnsChecker columnsChecker;
    private String[][] images;
    private String[] correction;
    private int cellWidth, cellHeight;
    private int[][] imagesCoordinates, columnsCoordinates, columnsDimensions;
    private float distanceToLeft, distanceToTop;
    private int[] dragLimits;

    public ColumnsActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        columnsChecker = new ColumnsChecker();

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
            images = new String[jsonImages.length()][];
            correction = new String[jsonCorrection.length()];
            dragLimits = new int[4];

            for (int i=0; i<images.length; i++) {
                JSONArray jsonCategoryImages = jsonImages.getJSONArray(i);
                images[i] = new String[jsonCategoryImages.length()];
                for (int j=0; j<images[i].length; j++) {
                    images[i][j] = jsonCategoryImages.getString(j);
                }
            }
            for (int i=0; i<jsonCorrection.length(); i++) {
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
        ConstraintLayout beforeAfterActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.columns_activity_template, contentContainer, false);

        TextView lTitle = (TextView) beforeAfterActivityTemplate.findViewById(R.id.left_title);
        TextView rTitle = (TextView) beforeAfterActivityTemplate.findViewById(R.id.right_title);
        lTitle.setText(leftColumnTitle);
        rTitle.setText(rightColumnTitle);

        if (contentContainer != null) {
            contentContainer.addView(beforeAfterActivityTemplate);

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

        if (contentContainer != null) {
            dragLimits[0] = 0;
            dragLimits[1] = 0;
            dragLimits[2] = contentContainer.getRight();
            dragLimits[3] = contentContainer.getBottom();
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }

        if (grid != null) {
            int left = grid.getLeft();
            int top = grid.getTop();

            /* piecesCoordinates will contain the images' coordinates */
            imagesCoordinates = new int[ColumnsConstants.NUMBER_OF_IMAGES][CommonConstants.AXIS_NUMBER];

            /* Cell center coordinates are calculated based on the upper left corner of the grid */
            int index = 0;
            for (int i=0; i<grid.getChildCount(); i++) {
                /* We only store even coordinates (cells 0, 2, 4...) */
                if (i%2 == 0) {
                    View cell = grid.getChildAt(i);
                    imagesCoordinates[index][0] = left + cell.getLeft() + cell.getWidth()/2;
                    imagesCoordinates[index][1] = top + cell.getTop() + cell.getHeight()/2;
                    index++;

                    cellWidth = cell.getWidth();
                    cellHeight = cell.getHeight();
                }
            }

            /* columnsCoordinates will contain the coordinates of the corners of the columns */
            columnsCoordinates = new int[ColumnsConstants.NUMBER_OF_COLUMNS_CORNERS][CommonConstants.AXIS_NUMBER];
            columnsDimensions = new int[ColumnsConstants.NUMBER_OF_COLUMNS_CORNERS][CommonConstants.AXIS_NUMBER];

            LinearLayout leftColumnContainer = (LinearLayout) gameParameters.findViewById(R.id.left_column_container);
            LinearLayout rightColumnContainer = (LinearLayout) gameParameters.findViewById(R.id.right_column_container);
            View leftColumn = gameParameters.findViewById(R.id.left_column);
            View rightColumn = gameParameters.findViewById(R.id.right_column);

            if ((leftColumn != null)&&(rightColumn != null)&&(leftColumnContainer != null)&&(rightColumnContainer != null)) {
                /* Coordinates of the corners of the columns */
                columnsCoordinates[ColumnsConstants.LEFT_COLUMN_INDEX][0] = leftColumnContainer.getLeft();
                columnsCoordinates[ColumnsConstants.LEFT_COLUMN_INDEX][1] = leftColumn.getTop();
                columnsCoordinates[ColumnsConstants.RIGHT_COLUMN_INDEX][0] = rightColumnContainer.getLeft();
                columnsCoordinates[ColumnsConstants.RIGHT_COLUMN_INDEX][1] = rightColumn.getTop();

                columnsDimensions[ColumnsConstants.LEFT_COLUMN_INDEX][0] = leftColumn.getWidth();
                columnsDimensions[ColumnsConstants.LEFT_COLUMN_INDEX][1] = leftColumn.getHeight();
                columnsDimensions[ColumnsConstants.RIGHT_COLUMN_INDEX][0] = rightColumn.getWidth();
                columnsDimensions[ColumnsConstants.RIGHT_COLUMN_INDEX][1] = rightColumn.getHeight();
            }
            else {
                new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "columns");
            }

            placeImages(contentContainer, images, ColumnsConstants.NUMBER_OF_IMAGES);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "grid");
        }
    }

    private void placeImages(RelativeLayout contentContainer, String[][] images, int imagesNumber) {
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
            placeImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i, correction[randomImagesIndex]);
        }

        /* As images are chosen randomly between all the available ones, we cannot take the length of the
           images array as reference. Instead of that, we use a number defined in activities_details */
        /* We start at 3 because indexes 0, 1 and 2 have been used just before */
        for (int i=images.length; i<imagesNumber; i++) {
            int randomImagesIndex = new Random().nextInt(images.length);
            /* images index can be 0, as all the subarrays have always the same number of images */
            int randomCategoryIndex = new Random().nextInt(images[0].length);

            while (occurrences[randomImagesIndex][randomCategoryIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
                randomCategoryIndex = new Random().nextInt(images[0].length);
            }
            occurrences[randomImagesIndex][randomCategoryIndex] = 1;

            placeImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i, correction[randomImagesIndex]);
        }

    }

    private void placeImage(RelativeLayout container, String imageName, int i, String correction) {
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));

        Drawable drawable = image.getDrawable();
        float scaleFactor;
        if (drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight()) {
            scaleFactor = (cellWidth*ColumnsConstants.CELL_FILLED_SPACE) / drawable.getIntrinsicWidth();
        }
        else {
            scaleFactor = (cellHeight*ColumnsConstants.CELL_FILLED_SPACE) / drawable.getIntrinsicHeight();
        }

        int width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
        int height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        image.setLayoutParams(layoutParams);

        imagesCoordinates[i][0] = imagesCoordinates[i][0] - width/2;
        imagesCoordinates[i][1] = imagesCoordinates[i][1] - height/2;
        image.setX(imagesCoordinates[i][0]);
        image.setY(imagesCoordinates[i][1]);
        String tag = i + "-" + correction;
        image.setTag(tag);

        container.addView(image);

        TouchListener touchListener = new TouchListener(ColumnsConstants.COLUMNS_TYPE, this);
        image.setOnTouchListener(touchListener);
    }

    protected void processTouchEvent(View view, MotionEvent event) {
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
                for (int i=0; i<columnsCoordinates.length; i++) {
                    if ((viewCenterX > columnsCoordinates[i][0])&&(viewCenterX < (columnsCoordinates[i][0]+columnsDimensions[i][0]))) {
                        if (viewCenterY > columnsCoordinates[i][1]) {
                            insideColumnIndex = i;

                            String imageCategory = view.getTag().toString().split("-")[1];
                            correctAnswer = columnsChecker.check(gameParameters, imageCategory, correction[i]);

                            break;
                        }
                    }
                }

                if (correctAnswer) {
                    /* If the view is not completely inside the box, we move it */
                    if (view.getX() < columnsCoordinates[insideColumnIndex][0]) {
                        view.setX(columnsCoordinates[insideColumnIndex][0]);
                    }
                    else if ((view.getX()+view.getWidth()) > (columnsCoordinates[insideColumnIndex][0]+columnsDimensions[insideColumnIndex][0])) {
                        view.setX(columnsCoordinates[insideColumnIndex][0]+columnsDimensions[insideColumnIndex][0]-view.getWidth());
                    }

                    if (view.getY() < columnsCoordinates[insideColumnIndex][1]) {
                        view.setY(columnsCoordinates[insideColumnIndex][1]);
                    }
                    else if ((view.getY()+view.getHeight()) > (columnsCoordinates[insideColumnIndex][1]+columnsDimensions[insideColumnIndex][1])) {
                        view.setY(columnsCoordinates[insideColumnIndex][1]+columnsDimensions[insideColumnIndex][1]-view.getHeight());
                    }
                }
                else {
                    ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", view.getX(), imagesCoordinates[index][0]);
                    animX.setDuration(1000);
                    ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", view.getY(), imagesCoordinates[index][1]);
                    animY.setDuration(1000);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(animX).with(animY);
                    animatorSet.start();
                }

                break;
            default:
                break;
        }
    }

}
