package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GuidedGameActivity extends AppCompatActivity {

    private LayoutInflater inflater;
    private ListView container;
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

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        unitsTitles = getResources().getStringArray(R.array.units_titles);
        activitiesTitles = getResources().getStringArray(R.array.activities_titles);
        activitiesImages = getResources().getStringArray(R.array.activities_images);

//        for (int i=0; i<TOTAL_UNITS; i++) {
//            View unitContainerView = inflater.inflate(R.layout.menu_unit_container_layout, container, false);
//
//            setUnitTitle(unitContainerView, i);
//            setUnitActivities(unitContainerView);
//
//            container.addView(unitContainerView);
//        }
//        inflater.inflate(R.layout.menu_unit_container_layout, container, true);

//        String[][] activitiesTitles = new String[TOTAL_UNITS][TOTAL_ACTIVITIES];
//        String[][] activitiesImages = new String[TOTAL_UNITS][TOTAL_ACTIVITIES];
//        for (int i=0; i<TOTAL_UNITS; i++) {
//            String title = UNIT + (i+1) + ACTIVITIES_TITLE;
//            String image = UNIT + (i+1) + ACTIVITIES_IMAGE;
//
//            activitiesTitles[i] = getResources().getStringArray(getResources().getIdentifier(title, "array", getPackageName()));
//            activitiesImages[i] = getResources().getStringArray(getResources().getIdentifier(image, "array", getPackageName()));
//        }

        String[] unitsTitles = getResources().getStringArray(R.array.units_titles);
        String[] activitiesTitles = getResources().getStringArray(R.array.activities_titles);
        String[] activitiesImages = getResources().getStringArray(R.array.activities_images);

        container = (ListView) findViewById(R.id.list_guided_activities);
        container.setAdapter(new CustomListAdapter(this, unitsTitles, activitiesTitles, activitiesImages));

    }

//    private void setUnitTitle(View unitContainerView, int i) {
//        TextView unitTitle = (TextView) unitContainerView.findViewById(R.id.unit_title);
//        unitTitle.setText(unitsTitles[i]);
//    }
//
//    private void setUnitActivities(View unitContainerView) {
//        TableLayout unitActivitiesTable = (TableLayout) unitContainerView.findViewById(R.id.unit_activities_table);
//
//        for (int i=0; i<unitActivitiesTable.getChildCount(); i++) {
//            TableRow unitActivitiesRow = (TableRow) unitActivitiesTable.getChildAt(i);
//            for (int j=0; j<unitActivitiesRow.getChildCount(); j++) {
//                LinearLayout activity = (LinearLayout) unitActivitiesRow.getChildAt(j);
//
//                TextView activityTitle = (TextView) activity.getChildAt(0);
//                activityTitle.setText(activitiesTitles[currentActivity]);
//                activityTitle.setTag(currentActivity);
//
//                ImageView activityImage = (ImageView) activity.getChildAt(1);
//                activityImage.setImageResource(getResources().getIdentifier(activitiesImages[currentActivity], "drawable", getPackageName()));
//
//                activity.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        TextView activity = (TextView) view.findViewById(R.id.activity_title);
//                        int activityTag = (int) activity.getTag();
//                        String activityTitle = activity.getText().toString();
//
//                        Intent intent = new Intent(context, GameParameters.class);
//                        intent.putExtra("activityTitle", activityTitle);
//                        intent.putExtra("activityNumber", activityTag);
//
//                        startActivity(intent);
//                    }
//                });
//
//                currentActivity++;
//            }
//        }
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
