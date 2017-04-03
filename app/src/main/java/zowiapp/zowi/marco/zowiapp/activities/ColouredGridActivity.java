package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ColouredGridConstants;
import zowiapp.zowi.marco.zowiapp.checker.ColouredGridChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class ColouredGridActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private ColouredGridChecker colouredGridChecker;
    private String activityTitle, activityDescription;
    private String[] images;
    private int[][] cells;
    private String[][] colouredCells;
    private JSONObject activityDetails;
    private int[][] coordinates;
    private int[] colouredCellsNumber;
    private int cellWidth, cellHeight;

    public ColouredGridActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        colouredGridChecker = new ColouredGridChecker();

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonCells = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_CELLS);
            JSONArray jsonImages = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonColouredCells = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_COLOUREDCELLS);
            cells = new int[jsonCells.length()][];
            images = new String[jsonImages.length()];
            colouredCells = new String[jsonColouredCells.length()][];
            colouredCellsNumber = new int[ColouredGridConstants.NUMBER_OF_COLORS];

            for (int i=0; i<cells.length; i++) {
                JSONArray jsonCellsArray = jsonCells.getJSONArray(i);
                JSONArray jsonColouredCellsArray = jsonColouredCells.getJSONArray(i);
                cells[i] = new int[jsonCellsArray.length()];
                colouredCells[i] = new String[jsonColouredCellsArray.length()];
                for (int j=0; j<cells[i].length; j++) {
                    cells[i][j] = jsonCellsArray.getInt(j);
                    colouredCells[i][j] = jsonColouredCellsArray.getString(j);
                }
                for (int j=0; j<colouredCells[i].length; j++) {
                    colouredCells[i][j] = jsonColouredCellsArray.getString(j);
                }
            }
            for (int i=0; i<images.length; i++) {
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
        ConstraintLayout colouredGridActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.coloured_grid_activity_template, contentContainer, false);
        ConstraintLayout grid = (ConstraintLayout) colouredGridActivityTemplate.findViewById(R.id.coloured_grid);

        /* In this activity, grid is always 4x4 */
        inflater.inflate(R.layout.grid_4x4_template, grid, true);
        coordinates = new int[ColouredGridConstants.COORDINATES_4X4_LENGTH][CommonConstants.AXIS_NUMBER];

        if (contentContainer != null) {
            contentContainer.addView(colouredGridActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(ColouredGridConstants.COLOUREDGRID_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

            /* Listener for the correction buttons */
            LinearLayout answersContainer = (LinearLayout) gameParameters.findViewById(R.id.answers_container);
            if (answersContainer != null) {
                for (int i=0; i<answersContainer.getChildCount(); i++) {
                    ConstraintLayout colorContainer = (ConstraintLayout) answersContainer.getChildAt(i);
                    ConstraintLayout colorButtonContainer = (ConstraintLayout) colorContainer.getChildAt(colorContainer.getChildCount()-1);
                    Button colorButton = (Button) colorButtonContainer.getChildAt(0);

                    colorButton.setTag(i);
                    colorButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int index = (int)view.getTag();
                            colouredGridChecker.check(gameParameters, index, colouredCellsNumber[index]+1);
                        }
                    });
                }
            }
            else {
                new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "answersContainer");
            }
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        /* 'grid' contains the 3x3 or 4x4 grid */
        ConstraintLayout gridContainer = (ConstraintLayout) gameParameters.findViewById(R.id.coloured_grid);

        /* Generation of the random index to choose one of the possible different activities */
        int randomIndex = new Random().nextInt(cells.length);

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

                /* The cells are coloured now, instead of looping again later */
                switch (colouredCells[randomIndex][i]) {
                    case "RED":
                        cell.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.red));
                        colouredCellsNumber[0]++;
                        break;
                    case "BLUE":
                        cell.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.blue));
                        colouredCellsNumber[1]++;
                        break;
                    case "GREEN":
                        cell.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.green));
                        colouredCellsNumber[2]++;
                        break;
                    default:
                        break;
                }
            }

            placeImages(contentContainer, cells[randomIndex], images);
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
            scaleFactor = (cellWidth* ActivityConstants.GridConstants.CELL_FILLED_SPACE) / drawable.getIntrinsicWidth();
        }
        else {
            scaleFactor = (cellHeight* ActivityConstants.GridConstants.CELL_FILLED_SPACE) / drawable.getIntrinsicHeight();
        }

        int width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
        int height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        image.setLayoutParams(layoutParams);
        image.setX(x - width/2);
        image.setY(y - height/2);

        container.addView(image);
    }

}
