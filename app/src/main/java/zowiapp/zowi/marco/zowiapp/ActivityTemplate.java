package zowiapp.zowi.marco.zowiapp;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

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
        if (description != null) {
            description.setText(activityDescription);
        }
    }

    protected void returnFromLayoutListener(ActivityType activityType, ActivityTemplate activity) {
        switch (activityType) {
            case GRID:
                GridActivity gridActivity = (GridActivity) activity;
                gridActivity.getElementsCoordinates();
                break;
            case COLUMNS:
                ColumnsActivity columnsActivity = (ColumnsActivity) activity;
                columnsActivity.getElementsCoordinates();
                break;
            default:
                break;
        }
    }

    protected void returnFromTouchListener(ActivityType activityType, ActivityTemplate activity, View view, MotionEvent event) {
        switch (activityType) {
            case GRID:
                GridActivity gridActivity = (GridActivity) activity;
                gridActivity.processTouchEvent(view, event);
                break;
            case COLUMNS:
                ColumnsActivity columnsActivity = (ColumnsActivity) activity;
                columnsActivity.processTouchEvent(view, event);
                break;
            default:
                break;
        }
    }

    protected abstract void getParameters();
    protected abstract void generateLayout();

}
