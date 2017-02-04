package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ColumnsConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.GameParameters;
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
    private String[] images;
    private JSONObject activityDetails;
    private int[][] piecesCoordinates;
    private int[][] columnCoordinates;
    float startX, startY, upperLimit = 0;

    public ColumnsActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
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
            leftColumnTitle = activityDetails.getString(ColumnsConstants.JSON_PARAMETER_LEFTTITLE);
            rightColumnTitle = activityDetails.getString(ColumnsConstants.JSON_PARAMETER_RIGHTTITLE);
            JSONArray jsonBAImages = activityDetails.getJSONArray(ColumnsConstants.JSON_PARAMETER_IMAGES);
            images = new String[jsonBAImages.length()];

            for (int i=0; i<jsonBAImages.length(); i++) {
                images[i] = jsonBAImages.getString(i);
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
        LinearLayout beforeAfterActivityTemplate = (LinearLayout) inflater.inflate(R.layout.columns_activity_template, contentContainer, false);

        TextView lTitle = (TextView) beforeAfterActivityTemplate.findViewById(R.id.left_title);
        TextView rTitle = (TextView) beforeAfterActivityTemplate.findViewById(R.id.right_title);
        lTitle.setText(leftColumnTitle);
        rTitle.setText(rightColumnTitle);

        if (contentContainer != null) {
            contentContainer.addView(beforeAfterActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(ColumnsConstants.COLUMNS_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        RelativeLayout imagesContainer = (RelativeLayout) gameParameters.findViewById(R.id.images_container);

        /* piecesCoordinates will contain the images' coordinates */
        piecesCoordinates = new int[ColumnsConstants.NUMBER_OF_IMAGES][CommonConstants.AXIS_NUMBER];
        /* columnsCoordinates will contain the coordinates of the corners of the columns */
        columnCoordinates = new int[ColumnsConstants.NUMBER_OF_COLUMNS_CORNERS][CommonConstants.AXIS_NUMBER];

        if (imagesContainer != null) {
            /* The first coordinates are the ones from the element in the center */
            int centerX = imagesContainer.getLeft() + imagesContainer.getWidth()/2;
            int centerY = imagesContainer.getTop() + imagesContainer.getHeight()/2;
            piecesCoordinates[0][0] = centerX - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
            piecesCoordinates[0][1] = centerY - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;

            /* Place the other images on the corners of a square */
            /* 'distance' is a intermediate distance between the center and the limits of the container */
            int distance = (imagesContainer.getWidth()/6)*2;
            for (int i=1; i<5; i++) {
                int x = (int) Math.round(centerX + distance*Math.cos(ColumnsConstants.CIRCUMFERENCE_INITIAL_POS*(Math.PI/180) + ColumnsConstants.CIRCUMFERENCE_INCREMENT*(Math.PI/180)*(i-1)));
                int y = (int) Math.round(centerY + distance*Math.sin(ColumnsConstants.CIRCUMFERENCE_INITIAL_POS*(Math.PI/180) + ColumnsConstants.CIRCUMFERENCE_INCREMENT*(Math.PI/180)*(i-1)));

                piecesCoordinates[i][0] = x - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
                piecesCoordinates[i][1] = y - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
            }

            View leftColumn = gameParameters.findViewById(R.id.left_column);
            View rightColumn = gameParameters.findViewById(R.id.right_column);

            if ((leftColumn != null)&&(rightColumn != null)) {
                /* Coordinates of the corners of the columns */
                columnCoordinates[ColumnsConstants.LEFT_COLUMN_INDEX][0] = leftColumn.getLeft() + leftColumn.getWidth();
                columnCoordinates[ColumnsConstants.LEFT_COLUMN_INDEX][1] = leftColumn.getTop();
                columnCoordinates[ColumnsConstants.RIGHT_COLUMN_INDEX][0] = leftColumn.getLeft() + leftColumn.getWidth() + imagesContainer.getWidth();
                columnCoordinates[ColumnsConstants.RIGHT_COLUMN_INDEX][1] = rightColumn.getTop();
            }
        }
        placeImages(contentContainer, images, ColumnsConstants.NUMBER_OF_IMAGES);
    }

    private void placeImages(RelativeLayout contentContainer, String[] images, int imagesNumber) {
        int[] occurrences = new int[images.length];
        for (int i=0; i<occurrences.length; i++) {
            occurrences[i] = 0;
        }

        for (int i=0; i<imagesNumber; i++) {
            int randomIndex = new Random().nextInt(occurrences.length);
            while (occurrences[randomIndex] == 1) {
                randomIndex = new Random().nextInt(occurrences.length);
            }
            occurrences[randomIndex] = 1;
            placeImage(contentContainer, images[randomIndex], piecesCoordinates[i][0], piecesCoordinates[i][1], i);
        }

    }

    private void placeImage(RelativeLayout container, String imageName, int x, int y, int i) {
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ColumnsConstants.COLUMNS_IMAGE_WIDTH_PX, ColumnsConstants.COLUMNS_IMAGE_WIDTH_PX);
        image.setLayoutParams(layoutParams);
        image.setX(x);
        image.setY(y);
        image.setTag(i);

        container.addView(image);

        TouchListener touchListener = new TouchListener(ColumnsConstants.COLUMNS_TYPE, this);
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
                    view.setX(piecesCoordinates[(int)view.getTag()][0]+distanceX);
                    view.setY(piecesCoordinates[(int)view.getTag()][1]+distanceY);

                    if (view.getY()<=0) {
                        upperLimit = event.getRawY();
                    }
                }
                else {
                    view.setX(piecesCoordinates[(int)view.getTag()][0]+distanceX);
                }
                break;
            case MotionEvent.ACTION_UP:
                upperLimit = 0;

                int leftColumnIndex = ColumnsConstants.LEFT_COLUMN_INDEX;
                int rightColumnIndex = ColumnsConstants.RIGHT_COLUMN_INDEX;
                float topLeftCornerX = view.getX();
                float topRightCornerX = view.getX() + view.getWidth();
                float topLeftCornerY = view.getY();
                float bottomLeftCornerY = view.getY() + view.getHeight();

                /* The view is placed inside the left column */
                if ((topLeftCornerX < columnCoordinates[leftColumnIndex][0])
                        &&(bottomLeftCornerY > columnCoordinates[leftColumnIndex][1])) {
                    Toast.makeText(gameParameters, "Columna izquierda", Toast.LENGTH_SHORT).show();

                    /* Actions to do if the image is not completely inside the column */
                    /* The image is on the corner */
                    if ((topRightCornerX > columnCoordinates[leftColumnIndex][0])
                            &&(topLeftCornerY < columnCoordinates[leftColumnIndex][1])) {
                        view.setX(columnCoordinates[leftColumnIndex][0]-view.getWidth());
                        view.setY(columnCoordinates[leftColumnIndex][1]);
                    }
                    /* The image is on the top edge */
                    else if (topLeftCornerY < columnCoordinates[leftColumnIndex][1]) {
                        view.setY(columnCoordinates[leftColumnIndex][1]);
                    }
                    /* The image is on the right edge */
                    else if (topRightCornerX > columnCoordinates[leftColumnIndex][0]) {
                        view.setX(columnCoordinates[leftColumnIndex][0]-view.getWidth());
                    }
                }
                /* The view is placed inside the right column */
                else if ((topRightCornerX > columnCoordinates[rightColumnIndex][0])
                        &&((view.getY()+view.getHeight()) > columnCoordinates[rightColumnIndex][1])) {
                    Toast.makeText(gameParameters, "Columna derecha", Toast.LENGTH_SHORT).show();

                    /* The image is on the corner */
                    if ((topLeftCornerX < columnCoordinates[rightColumnIndex][0])
                            &&(topLeftCornerY < columnCoordinates[rightColumnIndex][1])) {
                        view.setX(columnCoordinates[rightColumnIndex][0]);
                        view.setY(piecesCoordinates[rightColumnIndex][1]);
                    }
                    /* The image is on the top edge */
                    else if (topLeftCornerY < columnCoordinates[rightColumnIndex][1]) {
                        view.setY(columnCoordinates[rightColumnIndex][1]);
                    }
                    /* The image is on the left edge */
                    else if (topLeftCornerX < columnCoordinates[rightColumnIndex][0]) {
                        view.setX(columnCoordinates[rightColumnIndex][0]);
                    }
                }
                /* The element goes back to the original position */
//                else {
//                    view.setX(piecesCoordinates[(int)view.getTag()][0]);
//                    view.setY(piecesCoordinates[(int)view.getTag()][1]);
//                }

                /* Store the new coordinates */
                piecesCoordinates[(int)view.getTag()][0] = (int)view.getX();
                piecesCoordinates[(int)view.getTag()][1] = (int)view.getY();
                break;
            default:
                break;
        }
    }

}
