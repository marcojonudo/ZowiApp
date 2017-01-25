package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import zowiapp.zowi.marco.zowiapp.ActivityConstants.*;

public class GameParameters extends AppCompatActivity {

    private GameParameters context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity_template);

        context = this;

        Bundle parameter = getIntent().getExtras();
        String activityTitle = parameter.getString(CommonConstants.INTENT_PARAMETER_TITLE);
        int activityNumber = parameter.getInt(CommonConstants.INTENT_PARAMETER_NUMBER);

        /* Array with all the details needed to create the activity */
        String[] activitiesDetails = getResources().getStringArray(R.array.activities_details);

        chooseActivityType(activityTitle, activityNumber, activitiesDetails);
    }

    private void chooseActivityType(String activityTitle, int activityNumber, String[] activitiesDetails) {
        try {
            String json = activitiesDetails[activityNumber];
            JSONObject activityDetails = new JSONObject(json);

            ActivityType activityType = ActivityType.valueOf(activityDetails.getString(CommonConstants.JSON_PARAMETER_TYPE));
            switch (activityType) {
                case GRID:
                    new GridActivity(context, activityTitle, activityDetails);
                    break;
                case COLUMNS:
                    new ColumnsActivity(context, activityTitle, activityDetails);
                    break;
                default:
                    break;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
