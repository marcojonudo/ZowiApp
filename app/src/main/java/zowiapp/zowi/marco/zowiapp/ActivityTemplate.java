package zowiapp.zowi.marco.zowiapp;

import android.widget.TextView;

/**
 * Created by Marco on 24/01/2017.
 */
public abstract class ActivityTemplate {

    protected void setTitleDescription(GameParameters gameParameters, String activityTitle, String activityDescription) {
        TextView title = (TextView) gameParameters.findViewById(R.id.activity_title);
        TextView description = (TextView) gameParameters.findViewById(R.id.activity_description);
        title.setText(activityTitle);
        description.setText(activityDescription);
    }

    protected abstract void generateLayout();

}
