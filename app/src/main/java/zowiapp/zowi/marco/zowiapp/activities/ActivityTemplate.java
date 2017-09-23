package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.checker.CheckerTemplate;
import zowiapp.zowi.marco.zowiapp.checker.FoodPyramidChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.FoodPyramidConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

public abstract class ActivityTemplate {

    protected GameParameters gameParameters;
    protected LayoutInflater inflater;
    protected String activityTitle, activityDescription;
    protected JSONObject activityDetails;
    protected CheckerTemplate checker;
    protected ImagesHandler imagesHandler;
    String[] arrayImages;
    Point[] imagesCoordinates, containerCoordinates;
    String[][] doubleArrayImages;
    protected String[] correction;
    String[][] doubleArrayCorrection;
    int[] dragLimits;

    private float distanceToLeft, distanceToTop;
    private int insideColumnIndex;

    void initialiseCommonConstants(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected void setTitleDescription(GameParameters gameParameters, String activityTitle, String activityDescription) {
        TextView title = (TextView) gameParameters.findViewById(R.id.activity_title);
        TextView description = (TextView) gameParameters.findViewById(R.id.activity_description);

        if (title != null) {
            title.setText(activityTitle);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "title");
        }
        if (description != null) {
            description.setText(activityDescription);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "description");
        }
    }

    void setDragLimits(ViewGroup contentContainer) {
        dragLimits[0] = 0;
        dragLimits[1] = 0;
        dragLimits[2] = contentContainer.getRight();
        dragLimits[3] = contentContainer.getBottom();
    }

    public void returnFromLayoutListener(ActivityType activityType, ActivityTemplate activity) {
        switch (activityType) {
            case GRID:
                GridActivity gridActivity = (GridActivity) activity;
                gridActivity.getElementsCoordinates();
                break;
            case COLUMNS:
                ColumnsActivity columnsActivity = (ColumnsActivity) activity;
                columnsActivity.getElementsCoordinates();
                break;
            case PUZZLE:
                PuzzleActivity puzzleActivity = (PuzzleActivity) activity;
                puzzleActivity.getElementsCoordinates();
                break;
            case COLOURED_GRID:
                ColouredGridActivity colouredGridActivity = (ColouredGridActivity) activity;
                colouredGridActivity.getElementsCoordinates();
                break;
            case DRAG:
                DragActivity dragActivity = (DragActivity) activity;
                dragActivity.getElementsCoordinates();
                break;
            case FOODPYRAMID:
                FoodPyramidActivity foodPyramidActivity = (FoodPyramidActivity) activity;
                foodPyramidActivity.getElementsCoordinates();
                break;
            case MUSIC:
                MusicActivity musicActivity = (MusicActivity) activity;
                musicActivity.getElementsCoordinates();
                break;
            case SEEDS:
                SeedsActivity seedsActivity = (SeedsActivity) activity;
                seedsActivity.getElementsCoordinates();
                break;
            case ZOWI_EYES:
                ZowiEyesActivity zowiEyesActivity = (ZowiEyesActivity) activity;
                zowiEyesActivity.getElementsCoordinates();
                break;
            case GUIDE:
                GuideActivity guideActivity = (GuideActivity) activity;
                guideActivity.getElementsCoordinates();
                break;
            default:
                break;
        }
    }

    public void returnFromTouchListener(ActivityType activityType, ActivityTemplate activity, View view, MotionEvent event) {
        switch (activityType) {
            case GRID:
                GridActivity gridActivity = (GridActivity) activity;
                gridActivity.processTouchEvent(view, event);
                break;
            case COLUMNS:
                ColumnsActivity columnsActivity = (ColumnsActivity) activity;
                columnsActivity.processTouchEvent(view, event);
                break;
            case PUZZLE:
                PuzzleActivity puzzleActivity = (PuzzleActivity) activity;
                puzzleActivity.processTouchEvent(view, event);
                break;
            case DRAG:
                DragActivity dragActivity = (DragActivity) activity;
                dragActivity.processTouchEvent(view, event);
                break;
            case MEMORY:
                MemoryActivity memoryActivity = (MemoryActivity) activity;
                memoryActivity.processTouchEvent(view, event);
                break;
            case FOODPYRAMID:
                FoodPyramidActivity foodPyramidActivity = (FoodPyramidActivity) activity;
                foodPyramidActivity.processTouchEvent(view, event);
                break;
            case SEEDS:
                SeedsActivity seedsActivity = (SeedsActivity) activity;
                seedsActivity.processTouchEvent(view, event);
                break;
            default:
                break;
        }
    }

    String[] handleEvents(ActivityType activityType, View view, MotionEvent event, Point containerDimension, Point[] containerDimensions) {
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
                return downAction(activityType, view, event, headerTextHeight);
            case MotionEvent.ACTION_MOVE:
                moveAction(activityType, view, event, headerTextHeight);
                break;
            case MotionEvent.ACTION_UP:
                return checkViewInsideContainer(activityType, view, containerCoordinates, containerDimension, containerDimensions);
            default:
                break;
        }

        return null;
    }

    private String[] downAction(ActivityType activityType, View view, MotionEvent event, int headerTextHeight) {
        switch (activityType) {
            case MEMORY:
                if (((MemoryActivity)this).first) {
                    ((MemoryActivity)this).firstImageID = Integer.parseInt(view.getTag().toString().split(CommonConstants.TAG_SEPARATOR)[0]);
                    ((MemoryActivity)this).firstPosition = Integer.parseInt(view.getTag().toString().split(CommonConstants.TAG_SEPARATOR)[1]);
                    ((MemoryActivity)this).first = false;
                }
                else {
                    int secondImageID = Integer.parseInt(view.getTag().toString().split("-")[0]);
                    ((MemoryActivity)this).secondPosition = Integer.parseInt(view.getTag().toString().split("-")[1]);
                    return new String[]{String.valueOf(((MemoryActivity)this).firstImageID), String.valueOf(secondImageID), String.valueOf(((MemoryActivity)this).firstPosition), String.valueOf(((MemoryActivity)this).secondPosition)};
                }
                break;
            case PUZZLE:
                distanceToLeft = view.getX() - event.getRawX();
                distanceToTop = headerTextHeight + view.getY() - event.getRawY();

                int index = (int) view.getTag();
                float scaleFactorToPuzzle = ((PuzzleActivity)this).scaleFactorsToPuzzle[(int)view.getTag()];

                Animations.scaleAnimation(view, true, scaleFactorToPuzzle, PuzzleConstants.SCALE_ANIMATION_INCREASE_PIVOTS[index]);

                view.bringToFront();
                break;
            default:
                distanceToLeft = view.getX() - event.getRawX();
                distanceToTop = headerTextHeight + view.getY() - event.getRawY();

                view.bringToFront();
                break;
        }

        return null;
    }

    private void moveAction(ActivityType activityType, View view, MotionEvent event, int headerTextHeight) {
        switch (activityType) {
            case MEMORY:
                break;
            default:
                float left = event.getRawX() + distanceToLeft;
                float right = event.getRawX() + (view.getWidth()+ distanceToLeft);
                float top = event.getRawY() + distanceToTop - headerTextHeight;
                float bottom = event.getRawY() + (view.getHeight()+ distanceToTop);

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
        }
    }

    private String[] checkViewInsideContainer(ActivityType activityType, View view, Point[] containerCoordinates, Point containerDimension, Point[] containerDimensions) {
        float viewCenterX = view.getX() + view.getWidth()/2;
        float viewCenterY = view.getY() + view.getHeight()/2;
        int index = Integer.parseInt(view.getTag().toString().split(CommonConstants.TAG_SEPARATOR)[0]);

        String[] eventsResult = null;
        String imageCategory;
        switch (activityType) {
            case FOODPYRAMID:
                imageCategory = view.getTag().toString().split("-")[1];
                /* This equations make us know if the center of the view is between the two sides of the pyramid */
                int leftOrRight1 = (containerCoordinates[0].x- containerCoordinates[containerCoordinates.length-1].x) *
                        ((int)viewCenterY- containerCoordinates[containerCoordinates.length-1].y) -
                        (containerCoordinates[0].y- containerCoordinates[containerCoordinates.length-1].y) *
                                ((int)viewCenterX- containerCoordinates[containerCoordinates.length-1].x);
                int leftOrRight2 = (containerCoordinates[1].x- containerCoordinates[containerCoordinates.length-1].x) *
                        ((int)viewCenterY- containerCoordinates[containerCoordinates.length-1].y) -
                        (containerCoordinates[1].y- containerCoordinates[containerCoordinates.length-1].y) *
                                ((int)viewCenterX- containerCoordinates[containerCoordinates.length-1].x);
                /* This equations make us know if the center of the view is above or below the base of the pyramid */
                int aboveOrBelow = (containerCoordinates[1].x- containerCoordinates[0].x) *
                        ((int)viewCenterY- containerCoordinates[0].y) -
                        (containerCoordinates[1].y- containerCoordinates[0].y) *
                                ((int)viewCenterX- containerCoordinates[0].x);
                /* The center of the view is inside the pyramid */
                if ((leftOrRight1 < 0)&&(leftOrRight2 > 0) && (aboveOrBelow<0)) {
                    int step;
                    if (viewCenterY > containerCoordinates[2].y) {
                        step = 0;
                    }
                    else if (viewCenterY > containerCoordinates[3].y) {
                        if (viewCenterX > containerCoordinates[3].x) {
                            step = 1;
                        }
                        else {
                            step = 2;
                        }
                    }
                    else if (viewCenterY > containerCoordinates[4].y) {
                        if (viewCenterX > containerCoordinates[4].x) {
                            step = 3;
                        }
                        else {
                            step = 4;
                        }
                    }
                    else {
                        step = 5;
                    }
                    doubleArrayCorrection[FoodPyramidActivity.imagesCounter][0] = imageCategory;
                    doubleArrayCorrection[FoodPyramidActivity.imagesCounter][1] = correction[step];
                    ((FoodPyramidActivity)this).imageViews[FoodPyramidActivity.imagesCounter] = (ImageView) view;
                    FoodPyramidActivity.imagesCounter++;
                    if (FoodPyramidActivity.imagesCounter == FoodPyramidConstants.NUMBER_OF_IMAGES)
                        eventsResult = new String[0];
                }
                else {
                    Animations.translateAnimation(view, imagesCoordinates, index);
                }
                break;
            case PUZZLE:
                index = (int)view.getTag();
                ((PuzzleActivity)this).piecesCoordinates[index].set((int)view.getX(), (int)view.getY());
                PuzzleActivity.imagesCounter++;

                if (PuzzleActivity.imagesCounter == PuzzleConstants.PIECES_NUMBER)
                    /* eventsResult is created only to identify the end of pieces movements */
                    eventsResult = new String[0];
                break;
            case MEMORY:
                break;
            default:
                for (int i=0; i<containerCoordinates.length; i++) {
                    if ((viewCenterX > containerCoordinates[i].x)&&(viewCenterX < (containerCoordinates[i].x+(containerDimension != null ? containerDimension : containerDimensions[i]).x))) {
                        if (viewCenterY > containerCoordinates[i].y) {
                            insideColumnIndex = i;
                            imageCategory = view.getTag().toString().split("-")[1];
                            eventsResult = new String[]{String.valueOf(index), imageCategory, correction[i]};
                        }
                    }
                }
                if (eventsResult == null)
                    eventsResult = new String[]{String.valueOf(index), "1", "2"};
        }

        return eventsResult;
    }

    void lastImageMovement(ActivityType activityType, View view, Point containerDimension, Point[] containerDimensions, int index, boolean correctAnswer) {
        switch (activityType) {
            case MEMORY:
                if (correctAnswer) {
                    ConstraintLayout imagesGrid = (ConstraintLayout) gameParameters.findViewById(R.id.memory_images_container);

                    ImageView firstImage, secondImage;
                    View firstView, secondView;
                    if (imagesGrid != null) {
                        firstImage = (ImageView) ((FrameLayout) imagesGrid.getChildAt(((MemoryActivity)this).firstPosition)).getChildAt(1);
                        firstView = ((FrameLayout) imagesGrid.getChildAt(((MemoryActivity)this).firstPosition)).getChildAt(0);
                        secondImage = (ImageView) ((FrameLayout) imagesGrid.getChildAt(((MemoryActivity)this).secondPosition)).getChildAt(1);
                        secondView = ((FrameLayout) imagesGrid.getChildAt(((MemoryActivity)this).secondPosition)).getChildAt(0);

                        Animations.flip2WithDelay(gameParameters, firstImage, firstView, secondImage, secondView);
                    }
                    else {
                        new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "imagesGrid");
                    }
                }
                break;
            default:
                if (correctAnswer) {
                    /* If the view is not completely inside the box, we move it */
                    //TODO Añadir transición en caso de respuesta correcta
                    if (view.getX() < containerCoordinates[insideColumnIndex].x)
                        view.setX(containerCoordinates[insideColumnIndex].x);
                    else if ((view.getX()+view.getWidth()) > (containerCoordinates[insideColumnIndex].x+(containerDimension != null ? containerDimension : containerDimensions[insideColumnIndex]).x))
                        view.setX(containerCoordinates[insideColumnIndex].x+(containerDimension != null ? containerDimension : containerDimensions[insideColumnIndex]).x-view.getWidth());

                    if (view.getY() < containerCoordinates[insideColumnIndex].y)
                        view.setY(containerCoordinates[insideColumnIndex].y);
                    else if ((view.getY()+view.getHeight()) > (containerCoordinates[insideColumnIndex].y+(containerDimension != null ? containerDimension : containerDimensions[insideColumnIndex]).y))
                        view.setY(containerCoordinates[insideColumnIndex].y+(containerDimension != null ? containerDimension : containerDimensions[insideColumnIndex]).y-view.getHeight());
                }
                else {
                    Animations.translateAnimation(view, imagesCoordinates, index);
                }
        }

    }

    protected abstract void getParameters();
    protected abstract void generateLayout();

}
