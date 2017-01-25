package zowiapp.zowi.marco.zowiapp;

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

import zowiapp.zowi.marco.zowiapp.ActivityConstants.ColumnsConstants;
import zowiapp.zowi.marco.zowiapp.ActivityConstants.CommonConstants;

/**
 * Created by Marco on 24/01/2017.
 */
public class ColumnsActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription, leftColumnTitle, rightColumnTitle;
    private String[] images;
    private JSONObject activityDetails;
    private int[][] coordinates;
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
        LinearLayout beforeAfterActivityTemplate = (LinearLayout) inflater.inflate(R.layout.before_after_activity_template, contentContainer, false);

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

        /* baCoordinates will contain the images' coordinates, and the ones from the corners of the
           columns */
        coordinates = new int[images.length+ColumnsConstants.COLUMNS_INCREMENT][CommonConstants.AXIS_NUMBER];

        /* The first coordinates are the ones from the element in the center */
        int centerX = imagesContainer.getLeft() + imagesContainer.getWidth()/2;
        int centerY = imagesContainer.getTop() + imagesContainer.getHeight()/2;
        coordinates[0][0] = centerX - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
        coordinates[0][1] = centerY - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;

        /* Place the other images on the corners of a square */
        /* 'distance' is a intermediate distance between the center and the limits of the container */
        int distance = (imagesContainer.getWidth()/6)*2;
        for (int i=1; i<5; i++) {
            int x = (int) Math.round(centerX + distance*Math.cos(ColumnsConstants.CIRCUMFERENCE_INITIAL_POS*(Math.PI/180) + ColumnsConstants.CIRCUMFERENCE_INCREMENT*(Math.PI/180)*(i-1)));
            int y = (int) Math.round(centerY + distance*Math.sin(ColumnsConstants.CIRCUMFERENCE_INITIAL_POS*(Math.PI/180) + ColumnsConstants.CIRCUMFERENCE_INCREMENT*(Math.PI/180)*(i-1)));

            coordinates[i][0] = x - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
            coordinates[i][1] = y - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
        }

        View leftColumn = gameParameters.findViewById(R.id.left_column);
        View rightColumn = gameParameters.findViewById(R.id.right_column);

        /* Coordinates of the corners of the columns */
        coordinates[coordinates.length-ColumnsConstants.LEFT_COL_INDEX_ADJUSTMENT][0] = leftColumn.getLeft() + leftColumn.getWidth();
        coordinates[coordinates.length-ColumnsConstants.LEFT_COL_INDEX_ADJUSTMENT][1] = leftColumn.getTop();
        coordinates[coordinates.length-ColumnsConstants.RIGHT_COL_INDEX_ADJUSTMENT][0] = leftColumn.getLeft() + leftColumn.getWidth() + imagesContainer.getWidth();
        coordinates[coordinates.length-ColumnsConstants.RIGHT_COL_INDEX_ADJUSTMENT][1] = rightColumn.getTop();

        placeImages(contentContainer, images);
    }

    private void placeImages(RelativeLayout contentContainer, String[] images) {
        for (int i=0; i<images.length; i++) {
            placeImage(contentContainer, images[i], coordinates[i][0], coordinates[i][1], i);
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
            case MotionEvent.ACTION_UP:
                        /* The view is placed inside the left column */
                int leftColumnIndex = coordinates.length-ColumnsConstants.LEFT_COL_INDEX_ADJUSTMENT;
                int rightColumnIndex = coordinates.length-ColumnsConstants.RIGHT_COL_INDEX_ADJUSTMENT;
                float topLeftCornerX = view.getX();
                float topRightCornerX = view.getX() + view.getWidth();
                float topLeftCornerY = view.getY();
                float bottomLeftCornerY = view.getY() + view.getHeight();

                if ((topLeftCornerX < coordinates[leftColumnIndex][0])
                        &&(bottomLeftCornerY > coordinates[leftColumnIndex][1])) {
                    Toast.makeText(gameParameters, "Columna izquierda", Toast.LENGTH_SHORT).show();

                            /* Actions to do if the image is not completely inside the column */
                            /* The image is on the corner */
                    if ((topRightCornerX > coordinates[leftColumnIndex][0])
                            &&(topLeftCornerY < coordinates[leftColumnIndex][1])) {
                        view.setX(coordinates[leftColumnIndex][0]-view.getWidth());
                        view.setY(coordinates[leftColumnIndex][1]);
                    }
                            /* The image is on the top edge */
                    else if (topLeftCornerY < coordinates[leftColumnIndex][1]) {
                        view.setY(coordinates[leftColumnIndex][1]);
                    }
                            /* The image is on the right edge */
                    else if (topRightCornerX > coordinates[leftColumnIndex][0]) {
                        view.setX(coordinates[leftColumnIndex][0]-view.getWidth());
                    }
                }
                        /* The view is placed inside the right column */
                else if ((topRightCornerX > coordinates[rightColumnIndex][0])
                        &&((view.getY()+view.getHeight()) > coordinates[rightColumnIndex][1])) {
                    Toast.makeText(gameParameters, "Columna derecha", Toast.LENGTH_SHORT).show();

                            /* The image is on the corner */
                    if ((topLeftCornerX < coordinates[rightColumnIndex][0])
                            &&(topLeftCornerY < coordinates[rightColumnIndex][1])) {
                        view.setX(coordinates[rightColumnIndex][0]);
                        view.setY(coordinates[rightColumnIndex][1]);
                    }
                            /* The image is on the top edge */
                    else if (topLeftCornerY < coordinates[rightColumnIndex][1]) {
                        view.setY(coordinates[rightColumnIndex][1]);
                    }
                            /* The image is on the left edge */
                    else if (topLeftCornerX < coordinates[rightColumnIndex][0]) {
                        view.setX(coordinates[rightColumnIndex][0]);
                    }
                }
                        /* The element goes back to the original position */
                else {
                    view.setX(coordinates[(int)view.getTag()][0]);
                    view.setY(coordinates[(int)view.getTag()][1]);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                        /* The distance of the element to the start point is calculated when the user
                           moves it */
                float distanceX = event.getRawX() - startX;
                float distanceY = event.getRawY() - startY;

                        /* Mechanism to avoid the element to move behind the title and description
                           It is only moved when it is in 'contentContainer' */
                if (event.getRawY() > upperLimit) {
                    view.setX(coordinates[(int)view.getTag()][0]+distanceX);
                    view.setY(coordinates[(int)view.getTag()][1]+distanceY);

                    if (view.getY()<=0) {
                        upperLimit = event.getRawY();
                    }
                }
                else {
                    view.setX(coordinates[(int)view.getTag()][0]+distanceX);
                }
                break;
            default:
                break;
        }
    }

}
