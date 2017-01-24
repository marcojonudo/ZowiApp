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
    private GameParameters gameParameters;
    private String[] images;

    public LayoutListener(ActivityType activityType, View listenerElement, GameParameters gameParameters, String[] images) {
        this.activityType = activityType;
        this.listenerElemenet = listenerElement;
        this.gameParameters = gameParameters;
        this.images = images;
    }

    @Override
    public void onGlobalLayout() {
        listenerElemenet.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        gameParameters.hola(activityType,);
    }
}
