package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.*;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.activities.ColouredGridActivity;
import zowiapp.zowi.marco.zowiapp.activities.ColumnsActivity;
import zowiapp.zowi.marco.zowiapp.activities.DragActivity;
import zowiapp.zowi.marco.zowiapp.activities.FoodPyramidActivity;
import zowiapp.zowi.marco.zowiapp.activities.GridActivity;
import zowiapp.zowi.marco.zowiapp.activities.GuideActivity;
import zowiapp.zowi.marco.zowiapp.activities.LogicBlocksActivity;
import zowiapp.zowi.marco.zowiapp.activities.MemoryActivity;
import zowiapp.zowi.marco.zowiapp.activities.MusicActivity;
import zowiapp.zowi.marco.zowiapp.activities.OperationsActivity;
import zowiapp.zowi.marco.zowiapp.activities.PuzzleActivity;
import zowiapp.zowi.marco.zowiapp.activities.SeedsActivity;
import zowiapp.zowi.marco.zowiapp.activities.ZowiEyesActivity;

public class GameParameters extends AppCompatActivity {

    private GameParameters context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity_template);

        context = this;

        Bundle parameter = getIntent().getExtras();
        String categoryType = parameter.getString(CommonConstants.INTENT_PARAMETER_TYPE);
        String activityTitle = parameter.getString(CommonConstants.INTENT_PARAMETER_TITLE);
        int activityNumber = parameter.getInt(CommonConstants.INTENT_PARAMETER_NUMBER);

        /* Array with all the details needed to create the activity */
        String[] activitiesDetails = null;
        if (categoryType != null)
            activitiesDetails = categoryType.equals(CommonConstants.GUIDED) ? getResources().getStringArray(R.array.guided_activities_details) : getResources().getStringArray(R.array.free_activities_details);

        chooseActivityCategory(categoryType, activityTitle, activityNumber, activitiesDetails);
    }

    private void chooseActivityCategory(String categoryType, String activityTitle, int activityNumber, String[] activitiesDetails) {
        try {
            String json = activitiesDetails[activityNumber];
            JSONObject activityDetails = new JSONObject(json);

            ActivityType activityType = ActivityType.valueOf(activityDetails.getString(CommonConstants.JSON_PARAMETER_TYPE));

            if (categoryType.equals(CommonConstants.GUIDED))
                chooseGuidedActivityType(activityType, activityTitle, activityDetails);
            else
                chooseFreeActivityType(activityType, activityTitle, activityDetails);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void chooseFreeActivityType(ActivityType activityType, String activityTitle, JSONObject activityDetails) {
        switch (activityType) {
            case LOGIC_BLOCKS:
                new LogicBlocksActivity(context, activityTitle, activityDetails);
                break;
            default:
                break;
        }
    }

    private void chooseGuidedActivityType(ActivityType activityType, String activityTitle, JSONObject activityDetails) {
        switch (activityType) {
            case GRID:
                new GridActivity(context, activityTitle, activityDetails);
                break;
            case COLUMNS:
                new ColumnsActivity(context, activityTitle, activityDetails);
                break;
            case OPERATIONS:
                new OperationsActivity(context, activityTitle, activityDetails);
                break;
            case PUZZLE:
                new PuzzleActivity(context, activityTitle, activityDetails);
                break;
            case GUIDE:
                new GuideActivity(context, activityTitle, activityDetails);
                break;
            case COLOURED_GRID:
                new ColouredGridActivity(context, activityTitle, activityDetails);
                break;
            case MUSIC:
                new MusicActivity(context, activityTitle, activityDetails);
                break;
            case DRAG:
                new DragActivity(context, activityTitle, activityDetails);
                break;
            case MEMORY:
                new MemoryActivity(context, activityTitle, activityDetails);
                break;
            case FOODPYRAMID:
                new FoodPyramidActivity(context, activityTitle, activityDetails);
                break;
            case SEEDS:
                new SeedsActivity(context, activityTitle, activityDetails);
                break;
            case ZOWI_EYES:
                new ZowiEyesActivity(context, activityTitle, activityDetails);
                break;
            default:
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
