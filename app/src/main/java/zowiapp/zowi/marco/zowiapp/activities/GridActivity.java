package zowiapp.zowi.marco.zowiapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GridConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.checker.GridChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;

public class GridActivity extends ActivityTemplate {

    private int[] obstacles, totalCells;
    private ArrayList<Integer> nextCells;
    private int gridSize, movementsNumber, zowiCell, destinyCell, nextCell, directionsIndex, out, completedSteps;
    private Point cellDimensions;
    private ArrayList<String> directions;
    private boolean startedMoving;
    private static final String ZOWI_NAME = "zowi-0";

    public GridActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new GridChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.GRID);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            gridSize = activityDetails.getInt(GridConstants.JSON_PARAMETER_GRIDSIZE);
            JSONArray jsonCells = activityDetails.getJSONArray(GridConstants.JSON_PARAMETER_CELLS);
            JSONArray jsonImages = activityDetails.getJSONArray(GridConstants.JSON_PARAMETER_IMAGES);
            cellDimensions = new Point();

            zowiCell = jsonCells.getInt(0);
            nextCell = zowiCell;
            destinyCell = jsonCells.getInt(jsonCells.length()-1);
            obstacles = new int[jsonCells.length()-2];
            totalCells = new int[jsonCells.length()];
            arrayImages = new String[jsonImages.length()];
            directions = new ArrayList<>();
            nextCells = new ArrayList<>();
            nextCells.add(zowiCell);
            movementsNumber = 0;
            directionsIndex = 0;
            startedMoving = false;
            out = 0;
            completedSteps = 0;

            int obstaclesIndex = 0;
            for (int i=0; i<jsonCells.length(); i++) {
                if (i!=0 && i!=jsonCells.length()-1) {
                    obstacles[obstaclesIndex] = jsonCells.getInt(i);
                    obstaclesIndex++;
                }
                totalCells[i] = jsonCells.getInt(i);

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
        ConstraintLayout gridActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.guided_grid_activity_template, contentContainer, false);
        ConstraintLayout grid = (ConstraintLayout) gridActivityTemplate.findViewById(R.id.grid);

        switch (gridSize) {
            /* 3x3 */
            case 1:
                /* The grid is added automatically to 'grid' because the third parameter is 'true' */
                inflater.inflate(R.layout.guided_grid_3x3_template, grid, true);
                imagesCoordinates = Functions.createEmptyPointArray(GridConstants.COORDINATES_3X3_LENGTH);
                break;
            /* 4x4 */
            case 2:
                inflater.inflate(R.layout.guided_grid_4x4_template, grid, true);
                imagesCoordinates = Functions.createEmptyPointArray(GridConstants.COORDINATES_4X4_LENGTH);
                break;
            default:
                break;
        }

        /* Set the listener that detects which section of the controls has been touched */
        /* Instead of setting the listener on 'controls', it is set on both arrayImages. This way, we don't
           have problems with the width of the rescaled image */
        Button innerControl = (Button) gridActivityTemplate.findViewById(R.id.inner_control);
        ImageView outerControl = (ImageView) gridActivityTemplate.findViewById(R.id.outer_control);
        Button paperBin = (Button) gridActivityTemplate.findViewById(R.id.paper_bin);
        TouchListener touchListener = new TouchListener(ActivityType.GRID, this);
        innerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zowiMovement();
            }
        });
        outerControl.setOnTouchListener(touchListener);
        paperBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout movementsGrid = (ConstraintLayout) gameParameters.findViewById(R.id.movements_grid);

                if (movementsGrid != null && startedMoving) {
                    int lastMovementIndex = 0;
                    while (movementsGrid.getChildAt(lastMovementIndex).getTag() == null)
                        lastMovementIndex++;

                    int arrowIndex = GridConstants.MAX_MOVEMENTS - lastMovementIndex;

                    if (arrowIndex > completedSteps) {
                        ImageView lastMovementView = (ImageView) movementsGrid.getChildAt(lastMovementIndex);
                        lastMovementView.setImageDrawable(null);
                        lastMovementView.setTag(null);

                        ConstraintLayout gameGrid = (ConstraintLayout) gameParameters.findViewById(R.id.grid);
                        if (gameGrid != null && out==0) {
                            ConstraintLayout grid = (ConstraintLayout) gameGrid.getChildAt(0);
                            View lastMovementCell = grid.getChildAt(nextCells.get(arrowIndex)-1);
                            lastMovementCell.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.white));
                        }
                        if (out != 0)
                            out--;

                        nextCells.remove(arrowIndex);
                        directions.remove(arrowIndex-1);
                        movementsNumber--;

                        zowiCell = nextCells.get((GridConstants.MAX_MOVEMENTS - 1) - lastMovementIndex);
                        nextCell = zowiCell;
                        if (zowiCell == nextCells.get(0))
                            startedMoving = false;
                    }
                }
                else {
                    Toast.makeText(gameParameters, "¡No hay movimientos que borrar!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (contentContainer != null) {
            contentContainer.addView(gridActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(ActivityType.GRID, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }
    }

    private void zowiMovement() {
        if (zowiCell == destinyCell) {
            Button innerControl = (Button) gameParameters.findViewById(R.id.inner_control);
            ImageView outerControl = (ImageView) gameParameters.findViewById(R.id.outer_control);
            Button paperBin = (Button) gameParameters.findViewById(R.id.paper_bin);
            if (innerControl != null)
                innerControl.setOnTouchListener(null);
            if (outerControl != null)
                outerControl.setOnTouchListener(null);
            if (paperBin != null)
                paperBin.setOnTouchListener(null);

            finishActivity(ActivityType.GRID, true);
            ZowiActions.sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
        }
        if (directionsIndex < directions.size()) {
            int nextCell = ((GridChecker) checker).checkMovement(gameParameters, gridSize, directions.get(directionsIndex), zowiCell, obstacles);

            if (nextCell != 0) {
                sendDataToZowi(directionsIndex != 0 ? directions.get(directionsIndex-1) : "UP", directions.get(directionsIndex));
                RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
                AnimatorSet zowiAnimations;
                if (contentContainer != null) {
                    View zowi = null;
                    for (int i=0; i<contentContainer.getChildCount(); i++) {
                        String tag = contentContainer.getChildAt(i).getTag() != null ? contentContainer.getChildAt(i).getTag().toString() : "";
                        if (tag.equals(ZOWI_NAME))
                            zowi = contentContainer.getChildAt(i);
                    }
                    if (zowi != null) {
                        Point[] movementCoordinates = Functions.createEmptyPointArray(imagesCoordinates.length);
                        for (int j=0; j<imagesCoordinates.length; j++)
                            movementCoordinates[j].set(imagesCoordinates[j].x - zowi.getWidth()/2, imagesCoordinates[j].y - zowi.getHeight()/2);

                        zowiAnimations = Animations.rotateAndTranslate(zowi, directionsIndex != 0 ? directions.get(directionsIndex-1) : "UP", directions.get(directionsIndex),
                                movementCoordinates, nextCell-1);

                        zowiCell = nextCell;
                        completedSteps++;

                        if (directionsIndex == directions.size()-1)
                            zowi.bringToFront();

                        if (zowiAnimations != null) {
                            zowiAnimations.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    directionsIndex++;
                                    zowiMovement();
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void sendDataToZowi(String actualDirection, String nextDirection) {
        String[] commands = new String[2];
        switch (nextDirection) {
            case "UP":
                switch (actualDirection) {
                    case "LEFT":
                        commands[0] = ZowiActions.TURN_RIGHT;
                        break;
                    case "RIGHT":
                        commands[0] = ZowiActions.TURN_LEFT;
                        break;
                    case "DOWN":
                        commands[0] = ZowiActions.TURN_LEFT;
                        commands[1] = ZowiActions.TURN_LEFT;
                        break;
                }
                break;
            case "LEFT":
                switch (actualDirection) {
                    case "UP":
                        commands[0] = ZowiActions.TURN_LEFT;
                        break;
                    case "RIGHT":
                        commands[0] = ZowiActions.TURN_LEFT;
                        commands[1] = ZowiActions.TURN_LEFT;
                        break;
                    case "DOWN":
                        commands[0] = ZowiActions.TURN_RIGHT;
                        break;
                }
                break;
            case "RIGHT":
                switch (actualDirection) {
                    case "LEFT":
                        commands[0] = ZowiActions.TURN_LEFT;
                        commands[1] = ZowiActions.TURN_LEFT;
                        break;
                    case "UP":
                        commands[0] = ZowiActions.TURN_RIGHT;
                        break;
                    case "DOWN":
                        commands[0] = ZowiActions.TURN_LEFT;
                        break;
                }
                break;
            case "DOWN":
                switch (actualDirection) {
                    case "LEFT":
                        commands[0] = ZowiActions.TURN_RIGHT;
                        break;
                    case "RIGHT":
                        commands[0] = ZowiActions.TURN_LEFT;
                        break;
                    case "UP":
                        commands[0] = ZowiActions.TURN_LEFT;
                        commands[1] = ZowiActions.TURN_LEFT;
                        break;
                }
                break;
        }

        for (String command : commands) {
            if (command != null)
                ZowiActions.sendDataToZowi(command);
        }
        ZowiActions.sendDataToZowi(ZowiActions.ZOWI_WALKS_FORWARD);
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        /* 'grid' contains the 3x3 or 4x4 grid */
        ConstraintLayout gridContainer = (ConstraintLayout) gameParameters.findViewById(R.id.grid);

        if (gridContainer != null) {
            ConstraintLayout gameGrid = (ConstraintLayout) gridContainer.getChildAt(0);

            int left = gridContainer.getLeft();
            int top = gridContainer.getTop();

            /* Cell center imagesCoordinates are calculated based on the upper left corner of the grid */
            for (int i=0; i<gameGrid.getChildCount(); i++) {
                View cell = gameGrid.getChildAt(i);
                imagesCoordinates[i].set(left + cell.getLeft() + cell.getWidth()/2, top + cell.getTop() + cell.getHeight()/2);

                cellDimensions.set(cell.getWidth(), cell.getHeight());
            }

            imagesHandler.loadGridImages(contentContainer, imagesCoordinates, cellDimensions, totalCells, arrayImages);
            gameGrid.getChildAt(zowiCell-1).setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.paleRed));
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "gridContainer");
        }
    }

    void processTouchEvent(View view, MotionEvent event) {
        String[] eventsResult = handleEvents(ActivityType.GRID, view, event, null, null);
        if (eventsResult != null) {
            if (movementsNumber < GridConstants.MAX_MOVEMENTS) {
                directions.add(eventsResult[0]);
                movementsNumber++;

                fillDirectionsGrid(eventsResult[0]);
                colourNextCell(eventsResult[0]);
            }
        }
    }

    private void colourNextCell(String newDirection) {
        ConstraintLayout gridContainer = (ConstraintLayout) gameParameters.findViewById(R.id.grid);

        if (gridContainer != null) {
            int nextCell = getNextCell(newDirection);

            ConstraintLayout grid = (ConstraintLayout) gridContainer.getChildAt(0);
            if (nextCell >= 0 && nextCell < (gridSize == 1 ? 10 : 17)) {
                View cell = grid.getChildAt(nextCell-1);

                if (out == 0)
                    cell.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.paleRed));
            }
        }
    }

    private int getNextCell(String newDirection) {
        boolean smallGrid = gridSize == 1;
        switch (newDirection) {
            case "UP":
                if (nextCell>(smallGrid ? 3 : 4) && out==0)
                    nextCell = nextCell - (smallGrid ? 3 : 4);
                else
                    out++;
                break;
            case "LEFT":
                if ((nextCell+(smallGrid ? 2 : 3))%(smallGrid ? 3 : 4) != 0 && out==0)
                    nextCell = nextCell - 1;
                else
                    out++;
                break;
            case "RIGHT":
                if (nextCell%(smallGrid ? 3 : 4) != 0 && out==0)
                    nextCell = nextCell + 1;
                else
                    out++;
                break;
            case "DOWN":
                if (nextCell<(smallGrid ? 7 : 13) && out==0)
                    nextCell = nextCell + (smallGrid ? 3 : 4);
                else
                    out++;
                break;
        }
        nextCells.add(nextCell);
        startedMoving = true;

        return nextCell;
    }

    private void fillDirectionsGrid(String newDirection) {
        ConstraintLayout movementsGrid = (ConstraintLayout) gameParameters.findViewById(R.id.movements_grid);

        if (movementsGrid != null) {
            int newDirectionIndex = 0;

            while (newDirectionIndex < GridConstants.MAX_MOVEMENTS-1 && movementsGrid.getChildAt(newDirectionIndex+1).getTag() == null)
                newDirectionIndex++;

            int resourceId = 0;
            switch (newDirection) {
                case "UP":
                    resourceId = R.drawable.grid_arrow_up;
                    break;
                case "LEFT":
                    resourceId = R.drawable.grid_arrow_left;
                    break;
                case "RIGHT":
                    resourceId = R.drawable.grid_arrow_right;
                    break;
                case "DOWN":
                    resourceId = R.drawable.grid_arrow_down;
                    break;
            }

            if (resourceId != 0) {
                ImageView movementCell = (ImageView) movementsGrid.getChildAt(newDirectionIndex);
                Picasso.with(gameParameters)
                        .load(resourceId)
                        .into(movementCell);
                movementCell.setTag("USED");
            }
        }
        else {
            Toast.makeText(gameParameters, "¡Has alcanzado el número máximo de movimientos!", Toast.LENGTH_LONG).show();
        }
    }

}
