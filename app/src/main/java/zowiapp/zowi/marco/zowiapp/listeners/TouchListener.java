package zowiapp.zowi.marco.zowiapp.listeners;

import android.view.MotionEvent;
import android.view.View;

import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.activities.ActivityTemplate;

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
