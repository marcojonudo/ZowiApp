package zowiapp.zowi.marco.zowiapp.listeners;

import android.view.View;
import android.view.ViewTreeObserver;

import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.activities.ActivityTemplate;

public class LayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private ActivityType activityType;
    private View listenerElemenet;
    private ActivityTemplate activity;

    public LayoutListener(ActivityType activityType, View listenerElement, ActivityTemplate activity) {
        this.activityType = activityType;
        this.listenerElemenet = listenerElement;
        this.activity = activity;
    }

    @Override
    public void onGlobalLayout() {
        listenerElemenet.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        activity.returnFromLayoutListener(activityType, activity);
    }
}
