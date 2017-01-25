package zowiapp.zowi.marco.zowiapp;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * Created by Marco on 24/01/2017.
 */
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
