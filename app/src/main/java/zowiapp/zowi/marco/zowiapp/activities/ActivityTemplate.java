package zowiapp.zowi.marco.zowiapp.activities;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;

/**
 * Created by Marco on 24/01/2017.
 */
public abstract class ActivityTemplate {

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

    protected abstract void getParameters();
    protected abstract void generateLayout();

}
