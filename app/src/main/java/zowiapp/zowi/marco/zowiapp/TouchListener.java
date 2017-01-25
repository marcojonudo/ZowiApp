package zowiapp.zowi.marco.zowiapp;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Marco on 24/01/2017.
 */
public class TouchListener implements View.OnTouchListener {

    private ActivityType activityType;
    private ActivityTemplate activity;

    public TouchListener(ActivityType activityType, ActivityTemplate activity) {
        this.activityType = activityType;
        this.activity = activity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        activity.returnFromTouchListener(activityType, activity, view, event);

        return true;
    }

}
