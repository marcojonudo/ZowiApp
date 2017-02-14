package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GridConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class GridActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private String[] images;
    private int[] cells;
    private int gridSize;
    private JSONObject activityDetails;
    private int[][] coordinates;

    public GridActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
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
            gridSize = activityDetails.getInt(GridConstants.JSON_PARAMETER_GRIDSIZE);
            JSONArray jsonCells = activityDetails.getJSONArray(GridConstants.JSON_PARAMETER_CELLS);
            JSONArray jsonImages = activityDetails.getJSONArray(GridConstants.JSON_PARAMETER_IMAGES);
            cells = new int[jsonCells.length()];
            images = new String[jsonImages.length()];

            for (int i=0; i<jsonCells.length(); i++) {
                cells[i] = jsonCells.getInt(i);
                images[i] = jsonImages.getString(i);
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
        ConstraintLayout gridActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.grid_activity_template, contentContainer, false);
//        GridLayout grid = (GridLayout) gridActivityTemplate.findViewById(R.id.grid);
//
//        switch (gridSize) {
//            /* 3x3 */
//            case 1:
//                /* The grid is added automatically to 'grid' because the third parameter is 'true' */
//                inflater.inflate(R.layout.grid_3x3_template, grid, true);
//                coordinates = new int[GridConstants.COORDINATES_3X3_LENGTH][CommonConstants.AXIS_NUMBER];
//                break;
//            /* 4x4 */
//            case 2:
//                inflater.inflate(R.layout.grid_4x4_template, grid, true);
//                coordinates = new int[GridConstants.COORDINATES_4X4_LENGTH][CommonConstants.AXIS_NUMBER];
//                break;
//            default:
//                break;
//        }
//
//        /* Set the listener that detects what section of the controls has been touched */
//        FrameLayout controls = (FrameLayout) gridActivityTemplate.findViewById(R.id.controls);
//        TouchListener touchListener = new TouchListener(GridConstants.GRID_TYPE, this);
//        controls.setOnTouchListener(touchListener);

        if (contentContainer != null) {
            contentContainer.addView(gridActivityTemplate);

//            LayoutListener layoutListener = new LayoutListener(GridConstants.GRID_TYPE, contentContainer, this);
//            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        /* 'grid' contains the 3x3 or 4x4 grid */
        GridLayout grid = (GridLayout) gameParameters.findViewById(R.id.grid);

        if (grid != null) {
            GridLayout gameGrid = (GridLayout) grid.getChildAt(0);

            coordinates[0][0] = grid.getLeft();
            coordinates[0][1] = grid.getTop();

            /* Values used to calculate the center of the cells */
            int halfCell = 0, rows = 0;
            switch (gridSize) {
                case 1:
                    halfCell = grid.getWidth()/6;
                    rows = 3;
                    break;
                case 2:
                    halfCell = grid.getWidth()/8;
                    rows = 4;
                    break;
                default:
                    break;
            }

            /* Cell center coordinates are calculated based on the upper left corner of the grid */
            for (int i=0; i<gameGrid.getChildCount(); i++) {
                coordinates[i+1][0] = coordinates[0][0] + (((i%rows)*2 + 1)*halfCell) - GridConstants.GRID_TRANSLATION_TO_CENTER;
                coordinates[i+1][1] = coordinates[0][1] + (((i/rows)*2 + 1)*halfCell) - GridConstants.GRID_TRANSLATION_TO_CENTER;
            }

            placeImages(contentContainer, cells, images);
        }
    }

    private void placeImages(RelativeLayout gridActivityTemplate, int[] cells, String[] images) {
        /* 'cells' contains a number between 1 and 9 or 16 that indicates the cells that will contain an image */
        /* 'images' contains the name of the resources */
        for (int i=0; i<cells.length; i++) {
            placeImage(gridActivityTemplate, images[i], coordinates[cells[i]][0], coordinates[cells[i]][1]);
        }

    }

    private void placeImage(RelativeLayout container, String imageName, int x, int y) {
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(GridConstants.GRID_IMAGE_WIDTH_PX, GridConstants.GRID_IMAGE_WIDTH_PX);
        image.setLayoutParams(layoutParams);
        image.setX(x);
        image.setY(y);

        container.addView(image);
    }

    protected void processTouchEvent(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                float rightCorner = view.getWidth();
                float center = rightCorner/2;
                /* 'innerControl' is not the same image as 'outerControl', so the radius is its width/2 */
                ImageView innerControl = (ImageView) gameParameters.findViewById(R.id.inner_control);
                double circumferenceRadius = innerControl.getWidth()/2;

                double distanceToCenter = Math.sqrt(Math.pow(x-center, 2)+Math.pow(y-center, 2));

                /* If coordinates are inside the circumference */
                if (distanceToCenter < circumferenceRadius) {
                    Toast.makeText(gameParameters, "Go!", Toast.LENGTH_SHORT).show();
                }
                else {
                    /* Lines ecuations are used to determine where the user has touched the control */
                    if (x>y) {
                        if (y < (rightCorner-x)) {
                            Toast.makeText(gameParameters, "Zona 1", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(gameParameters, "Zona 2", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        if (y > (rightCorner-x)) {
                            Toast.makeText(gameParameters, "Zona 3", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(gameParameters, "Zona 4", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

}
