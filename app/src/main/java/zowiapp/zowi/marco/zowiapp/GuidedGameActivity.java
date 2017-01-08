package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GuidedGameActivity extends AppCompatActivity {

    ListView listView;
    private final int TOTAL_UNITS = 5;
    private final int TOTAL_ACTIVITIES = 6;
    private final String UNIT = "unit";
    private final String ACTIVITIES_TITLE = "_activities_title";
    private final String ACTIVITIES_IMAGE = "_activities_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guided_game);

        String[][] activitiesTitles = new String[TOTAL_UNITS][TOTAL_ACTIVITIES];
        String[][] activitiesImages = new String[TOTAL_UNITS][TOTAL_ACTIVITIES];
        for (int i=0; i<TOTAL_UNITS; i++) {
            String title = UNIT + (i+1) + ACTIVITIES_TITLE;
            String image = UNIT + (i+1) + ACTIVITIES_IMAGE;

            activitiesTitles[i] = getResources().getStringArray(getResources().getIdentifier(title, "array", getPackageName()));
            activitiesImages[i] = getResources().getStringArray(getResources().getIdentifier(image, "array", getPackageName()));
        }

        String[] unitsTitles = getResources().getStringArray(R.array.units_title);

        listView = (ListView) findViewById(R.id.list_guided_activities);
        listView.setAdapter(new CustomListAdapter(this, unitsTitles, activitiesTitles, activitiesImages));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
