package zowiapp.zowi.marco.zowiapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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

public class GridActivity extends ActivityTemplate {

    private int[] cells, obstacles;
    private ArrayList<Integer> nextCells;
    private int gridSize, movementsNumber, zowiCell, destinyCell, directionsIndex;
    private Point cellDimensions;
    private ArrayList<String> directions;

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
            destinyCell = jsonCells.getInt(jsonCells.length()-1);
            cells = new int[jsonCells.length()];
            obstacles = new int[jsonCells.length()-2];
            arrayImages = new String[jsonImages.length()];
            directions = new ArrayList<>();
            nextCells = new ArrayList<>();
            movementsNumber = 0;
            directionsIndex = 0;

            for (int i=0; i<jsonCells.length(); i++) {
                cells[i] = jsonCells.getInt(i);
                arrayImages[i] = jsonImages.getString(i);
            }
            for (int i=1; i<jsonCells.length()-1; i++) {
                obstacles[i] = jsonCells.getInt(i);
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

                if (movementsGrid != null) {
                    int lastMovementIndex = 0;
                    while (movementsGrid.getChildAt(lastMovementIndex).getTag() == null)
                        lastMovementIndex++;

                    ImageView lastMovementView = (ImageView) movementsGrid.getChildAt(lastMovementIndex);
                    lastMovementView.setImageDrawable(null);
                    lastMovementView.setTag(null);

                    nextCells.remove((GridConstants.MAX_MOVEMENTS - 1) - lastMovementIndex);
                    zowiCell = nextCells.get((GridConstants.MAX_MOVEMENTS - 2) - lastMovementIndex);
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
        if (directionsIndex < directions.size()) {
            int nextCell = ((GridChecker) checker).checkMovement(gameParameters, gridSize, zowiCell, directions.get(directionsIndex), zowiCell, obstacles);

            if (nextCell != 0) {
                RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
                AnimatorSet zowiAnimations = null;
                if (contentContainer != null) {
                    View zowi = contentContainer.getChildAt(1);

                    Point[] movementCoordinates = Functions.createEmptyPointArray(imagesCoordinates.length);
                    for (int j=0; j<imagesCoordinates.length; j++)
                        movementCoordinates[j].set(imagesCoordinates[j].x - zowi.getWidth()/2, imagesCoordinates[j].y - zowi.getHeight()/2);

                    zowiAnimations = Animations.rotateAndTranslate(zowi, directionsIndex != 0 ? directions.get(directionsIndex-1) : "UP", directions.get(directionsIndex),
                            movementCoordinates, nextCell-1);
                }

                zowiCell = nextCell;

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

            imagesHandler.loadGridImages(contentContainer, imagesCoordinates, cellDimensions, cells, arrayImages);
//            placeImages(contentContainer, cells, arrayImages);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "gridContainer");
        }
    }

//    private void placeImages(RelativeLayout contentContainer, int[] cells, String[] images) {
//        /* 'cells' contains a number between 1 and 9 or 16 that indicates the cells that will contain an image */
//        /* 'arrayImages' contains the name of the resources */
//        for (int i=0; i<cells.length; i++) {
//            placeImage(contentContainer, images[i], imagesCoordinates[cells[i]-1].x, imagesCoordinates[cells[i]-1].y);
//        }
//
//    }
//
//    private void placeImage(RelativeLayout container, String imageName, int x, int y) {
//        ImageView image = new ImageView(gameParameters);
//        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, CommonConstants.DRAWABLE, gameParameters.getPackageName()));
//
//        Drawable drawable = image.getDrawable();
//        float scaleFactor;
//        if (drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight()) {
//            scaleFactor = (cellDimensions.x*GridConstants.CELL_FILLED_SPACE) / drawable.getIntrinsicWidth();
//        }
//        else {
//            scaleFactor = (cellDimensions.y*GridConstants.CELL_FILLED_SPACE) / drawable.getIntrinsicHeight();
//        }
//
//        int width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
//        int height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
//        image.setLayoutParams(layoutParams);
//        image.setX(x - width/2);
//        image.setY(y - height/2);
//
//        container.addView(image);
//    }

    void processTouchEvent(View view, MotionEvent event) {
        String[] eventsResult = handleEvents(ActivityType.GRID, view, event, null, null);
        if (eventsResult != null) {
            if (movementsNumber < GridConstants.MAX_MOVEMENTS) {
//                boolean correctMovement = checkCorrectMovement(eventsResult[0]);
//                if (correctMovement) {
                    directions.add(eventsResult[0]);
//                    directions.set(movementsNumber, eventsResult[0]);
                    movementsNumber++;

                    fillDirectionsGrid(eventsResult[0]);
//                    setNextCell(eventsResult[0]);
//                }
            }
        }
    }

    private boolean checkCorrectMovement(String newDirection) {
        boolean correctDirection = true;
        int cellsNumber = gridSize == 1 ? 9 : 16;
        int cellsStep = gridSize == 1 ? 3 : 4;
        switch (newDirection) {
            case "UP":
                int rightUpLimit = gridSize == 1 ? 4 : 5;
                if (zowiCell > 0 && zowiCell < rightUpLimit)
                    correctDirection = false;
                break;
            case "LEFT":
                for (int i=1; i<cellsNumber; i=i+cellsStep) {
                    if (zowiCell % i == 0)
                        correctDirection = false;
                }
                break;
            case "RIGHT":
                int checkStart = gridSize == 1 ? 3 : 4;
                for (int i=checkStart; i<cellsNumber; i=i+cellsStep) {
                    if (zowiCell % i == 0)
                        correctDirection = false;
                }
                break;
            case "DOWN":
                int leftDownLimit = gridSize == 1 ? 6 : 12;
                int rightDownLimit = gridSize == 1 ? 10 : 17;
                if (zowiCell > leftDownLimit && zowiCell < rightDownLimit)
                    correctDirection = false;
                break;
        }

        if (!correctDirection)
            Toast.makeText(gameParameters, "¡Cuidado, que sacas a Zowi fuera de la cuadrícula!", Toast.LENGTH_LONG).show();

        return correctDirection;
    }

    private void setNextCell(String newDirection) {
        if (nextCells.size() < GridConstants.MAX_MOVEMENTS) {
            int nextCell = 0;
            switch (newDirection) {
                case "UP":
                    nextCell = zowiCell - 3;
                    break;
                case "LEFT":
                    nextCell = zowiCell - 1;
                    break;
                case "RIGHT":
                    nextCell = zowiCell + 1;
                    break;
                case "DOWN":
                    nextCell = zowiCell + 3;
                    break;
            }

            nextCells.add(nextCell);
//            nextCells.set(movementsNumber, nextCell);
            zowiCell = nextCell;
        }
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
