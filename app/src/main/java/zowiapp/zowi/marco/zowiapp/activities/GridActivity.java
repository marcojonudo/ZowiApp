package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GridConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
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
    private int gridSize, cellWidth, cellHeight;
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
        ConstraintLayout grid = (ConstraintLayout) gridActivityTemplate.findViewById(R.id.grid);

        switch (gridSize) {
            /* 3x3 */
            case 1:
                /* The grid is added automatically to 'grid' because the third parameter is 'true' */
                inflater.inflate(R.layout.grid_3x3_template, grid, true);
                coordinates = new int[GridConstants.COORDINATES_3X3_LENGTH][CommonConstants.AXIS_NUMBER];
                break;
            /* 4x4 */
            case 2:
                inflater.inflate(R.layout.grid_4x4_template, grid, true);
                coordinates = new int[GridConstants.COORDINATES_4X4_LENGTH][CommonConstants.AXIS_NUMBER];
                break;
            default:
                break;
        }

        /* Set the listener that detects which section of the controls has been touched */
        /* Instead of setting the listener on 'controls', it is set on both images. This way, we don't
           have problems with the width of the rescaled image */
        ImageView innerControl = (ImageView) gridActivityTemplate.findViewById(R.id.inner_control);
        ImageView outerControl = (ImageView) gridActivityTemplate.findViewById(R.id.outer_control);
        TouchListener touchListener = new TouchListener(GridConstants.GRID_TYPE, this);
        innerControl.setOnTouchListener(touchListener);
        outerControl.setOnTouchListener(touchListener);

        if (contentContainer != null) {
            contentContainer.addView(gridActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(GridConstants.GRID_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        /* 'grid' contains the 3x3 or 4x4 grid */
        ConstraintLayout gridContainer = (ConstraintLayout) gameParameters.findViewById(R.id.grid);

        if (gridContainer != null) {
            ConstraintLayout gameGrid = (ConstraintLayout) gridContainer.getChildAt(0);

            int left = gridContainer.getLeft();
            int top = gridContainer.getTop();

            /* Cell center coordinates are calculated based on the upper left corner of the grid */
            for (int i=0; i<gameGrid.getChildCount(); i++) {
                View cell = gameGrid.getChildAt(i);
                coordinates[i][0] = left + cell.getLeft() + cell.getWidth()/2;
                coordinates[i][1] = top + cell.getTop() + cell.getHeight()/2;

                cellWidth = cell.getWidth();
                cellHeight = cell.getHeight();
            }

            placeImages(contentContainer, cells, images);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "gridContainer");
        }
    }

    private void placeImages(RelativeLayout contentContainer, int[] cells, String[] images) {
        /* 'cells' contains a number between 1 and 9 or 16 that indicates the cells that will contain an image */
        /* 'images' contains the name of the resources */
        for (int i=0; i<cells.length; i++) {
            placeImage(contentContainer, images[i], coordinates[cells[i]-1][0], coordinates[cells[i]-1][1]);
        }

    }

    private void placeImage(RelativeLayout container, String imageName, int x, int y) {
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));

        Drawable drawable = image.getDrawable();
        float scaleFactor;
        if (drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight()) {
            scaleFactor = (cellWidth*GridConstants.CELL_FILLED_SPACE) / drawable.getIntrinsicWidth();
        }
        else {
            scaleFactor = (cellHeight*GridConstants.CELL_FILLED_SPACE) / drawable.getIntrinsicHeight();
        }

        int width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
        int height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        image.setLayoutParams(layoutParams);
        image.setX(x - width/2);
        image.setY(y - height/2);

        container.addView(image);
    }

    void processTouchEvent(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                float rightCorner = view.getWidth();
                String tag = view.getTag().toString();

                /* The tag determines which image has been touched */
                if (tag.equals(GridConstants.INNER)) {
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
