package zowiapp.zowi.marco.zowiapp.activities;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
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
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class ColouredGridActivity extends ActivityTemplate {

    private Point[] gridImagesCoordinates;
    private String[][] colouredCells;
    private int[] colouredCellsNumber;
    private int[][] elementsCells;
    private Point cellDimensions;

    public ColouredGridActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new ColouredGridChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.COLOURED_GRID);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonCells = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_CELLS);
            JSONArray jsonImages = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonColouredCells = activityDetails.getJSONArray(ColouredGridConstants.JSON_PARAMETER_COLOUREDCELLS);

            elementsCells = new int[jsonCells.length()][];
            arrayImages = new String[jsonImages.length()];
            colouredCells = new String[jsonColouredCells.length()][];
            colouredCellsNumber = new int[ColouredGridConstants.NUMBER_OF_COLORS];
            imagesCoordinates = Functions.createEmptyPointArray(ColouredGridConstants.COORDINATES_4X4_LENGTH);
            gridImagesCoordinates = Functions.createEmptyPointArray(arrayImages.length);
            cellDimensions = new Point();

            for (int i=0; i< elementsCells.length; i++) {
                JSONArray jsonCellsArray = jsonCells.getJSONArray(i);
                JSONArray jsonColouredCellsArray = jsonColouredCells.getJSONArray(i);
                elementsCells[i] = new int[jsonCellsArray.length()];
                colouredCells[i] = new String[jsonColouredCellsArray.length()];

                for (int j=0; j<elementsCells[i].length; j++) {
                    elementsCells[i][j] = jsonCellsArray.getInt(j);
                }
                for (int j=0; j<colouredCells[i].length; j++) {
                    colouredCells[i][j] = jsonColouredCellsArray.getString(j);
                }
            }
            for (int i=0; i<arrayImages.length; i++) {
                arrayImages[i] = jsonImages.getString(i);
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
        ConstraintLayout colouredGridActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.guided_coloured_grid_activity_template, contentContainer, false);
        ConstraintLayout grid = (ConstraintLayout) colouredGridActivityTemplate.findViewById(R.id.coloured_grid);

        /* In this activity, grid is always 4x4 */
        inflater.inflate(R.layout.guided_grid_4x4_template, grid, true);

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
                            ((ColouredGridChecker) checker).check(gameParameters, index, colouredCellsNumber[index]+1);
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
        int randomIndex = new Random().nextInt(elementsCells.length);

        if (gridContainer != null) {
            ConstraintLayout gameGrid = (ConstraintLayout) gridContainer.getChildAt(0);

            int left = gridContainer.getLeft();
            int top = gridContainer.getTop();

            /* Cell center coordinates are calculated based on the upper left corner of the grid */
            for (int i=0; i<gameGrid.getChildCount(); i++) {
                View cell = gameGrid.getChildAt(i);
                imagesCoordinates[i].set(left + cell.getLeft() + cell.getWidth()/2, top + cell.getTop() + cell.getHeight()/2);
                cellDimensions.set(cell.getWidth(), cell.getHeight());

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

            /* The coordinates of the images to be loaded into the grid are saved */
            for (int i=0; i<gridImagesCoordinates.length; i++)
                gridImagesCoordinates[i] = imagesCoordinates[elementsCells[randomIndex][i]];

            imagesHandler.loadGridImages(contentContainer, arrayImages, gridImagesCoordinates, cellDimensions);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "gridContainer");
        }
    }

}
