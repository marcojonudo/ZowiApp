package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GuidedGameActivity extends AppCompatActivity {

    private LayoutInflater inflater;
    private LinearLayout container;
    private GuidedGameActivity context = this;
    private String[] unitsTitles, activitiesTitles, activitiesImages;

    private final int TOTAL_UNITS = 5;
    private final int TOTAL_ACTIVITIES = 6;
    private int currentActivity = 0;
    private final String UNIT = "unit";
    private final String ACTIVITIES_TITLE = "_activities_title";
    private final String ACTIVITIES_IMAGE = "_activities_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guided_game);

        context = this;

        container = (LinearLayout) findViewById(R.id.guided_game_container);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        unitsTitles = getResources().getStringArray(R.array.units_titles);
        activitiesTitles = getResources().getStringArray(R.array.activities_titles);
        activitiesImages = getResources().getStringArray(R.array.activities_images);

        for (int i=0; i<TOTAL_UNITS; i++) {
            View unitContainerView = inflater.inflate(R.layout.unit_container_layout, container, false);

            setUnitTitle(unitContainerView, i);
            setUnitActivities(unitContainerView);

            container.addView(unitContainerView);
        }

    }

    private void setUnitTitle(View unitContainerView, int i) {
        TextView unitTitle = (TextView) unitContainerView.findViewById(R.id.unit_title);
        unitTitle.setText(unitsTitles[i]);
    }

    private void setUnitActivities(View unitContainerView) {
        TableLayout unitActivitiesTable = (TableLayout) unitContainerView.findViewById(R.id.unit_activities_table);

        for (int i=0; i<unitActivitiesTable.getChildCount(); i++) {
            TableRow unitActivitiesRow = (TableRow) unitActivitiesTable.getChildAt(i);
            for (int j=0; j<unitActivitiesRow.getChildCount(); j++) {
                LinearLayout activity = (LinearLayout) unitActivitiesRow.getChildAt(j);

                TextView activityTitle = (TextView) activity.getChildAt(0);
                activityTitle.setText(activitiesTitles[currentActivity]);
                activityTitle.setTag(currentActivity);

                ImageView activityImage = (ImageView) activity.getChildAt(1);
                activityImage.setImageResource(getResources().getIdentifier(activitiesImages[currentActivity], "drawable", getPackageName()));

                activity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView activity = (TextView) view.findViewById(R.id.activity_title);
                        int activityTag = (int) activity.getTag();
                        String activityTitle = activity.getText().toString();

                        Intent intent = new Intent(context, GameParameters.class);
                        intent.putExtra("activityTitle", activityTitle);
                        intent.putExtra("activityNumber", activityTag);

                        startActivity(intent);
                    }
                });

                currentActivity++;
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
