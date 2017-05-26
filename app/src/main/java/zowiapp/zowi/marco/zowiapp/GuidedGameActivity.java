package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.ListView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.utils.Layout;

public class GuidedGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guided_game);

        String[] unitsTitles = getResources().getStringArray(R.array.units_titles);
        String[] activitiesTitles = getResources().getStringArray(R.array.activities_titles);
        String[] activitiesImages = getResources().getStringArray(R.array.activities_images);

        ListView container = (ListView) findViewById(R.id.list_guided_activities);
        if (container != null)
            container.setAdapter(new CustomListAdapter(this, unitsTitles, activitiesTitles, activitiesImages));

        final LinearLayout guidedGameScroller = (LinearLayout) findViewById(R.id.guided_game_scroller);
        if (guidedGameScroller != null)
            Layout.drawOverlay(this, guidedGameScroller);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
