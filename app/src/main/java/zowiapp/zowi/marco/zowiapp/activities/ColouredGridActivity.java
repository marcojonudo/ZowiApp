package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ColouredGridConstants;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class ColouredGridActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private String[] images;
    private int[] cells;
    private String[] colouredCells;
    private JSONObject activityDetails;
    private int[][] coordinates;

    public ColouredGridActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
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
            JSONArray jsonCells = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_CELLS);
            JSONArray jsonImages = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonColouredCells = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_COLOUREDCELLS);
            cells = new int[jsonCells.length()];
            images = new String[jsonImages.length()];
            colouredCells = new String[jsonColouredCells.length()];

            for (int i=0; i<cells.length; i++) {
                cells[i] = jsonCells.getInt(i);
                images[i] = jsonImages.getString(i);
            }
            for (int i=0; i<colouredCells.length; i++) {
                colouredCells[i] = jsonColouredCells.getString(i);
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
        LinearLayout colouredGridActivityTemplate = (LinearLayout) inflater.inflate(R.layout.coloured_grid_activity_template, contentContainer, false);
        GridLayout grid = (GridLayout) colouredGridActivityTemplate.findViewById(R.id.grid);

        /* In this activity, grid is always 4x4 */
        inflater.inflate(R.layout.grid_4x4_template, grid, true);
        coordinates = new int[ColouredGridConstants.COORDINATES_4X4_LENGTH][CommonConstants.AXIS_NUMBER];


        if (contentContainer != null) {
            contentContainer.addView(colouredGridActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(ColouredGridConstants.COLOUREDGRID_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
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

            /* Values used to calculate the center of the cells (4x4 grid) */
            int halfCell = grid.getWidth()/8;
            int rows = 4;

            /* Cell center coordinates are calculated based on the upper left corner of the grid */
            for (int i=0; i<gameGrid.getChildCount(); i++) {
                coordinates[i+1][0] = coordinates[0][0] + (((i%rows)*2 + 1)*halfCell) - ColouredGridConstants.GRID_TRANSLATION_TO_CENTER;
                coordinates[i+1][1] = coordinates[0][1] + (((i/rows)*2 + 1)*halfCell) - ColouredGridConstants.GRID_TRANSLATION_TO_CENTER;

                /* The cells are coloured now, instead of looping again later */
                View cell = gameGrid.getChildAt(i);
                switch (colouredCells[i]) {
                    case "RED":
                        cell.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.red));
                        break;
                    case "BLUE":
                        cell.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.blue));
                        break;
                    case "GREEN":
                        cell.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.green));
                        break;
                    default:
                        break;
                }
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ColouredGridConstants.GRID_IMAGE_WIDTH_PX, ColouredGridConstants.GRID_IMAGE_WIDTH_PX);
        image.setLayoutParams(layoutParams);
        image.setX(x);
        image.setY(y);

        container.addView(image);
    }

}
