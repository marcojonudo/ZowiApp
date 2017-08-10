package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.ListView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.adapters.FreeGameListAdapter;
import zowiapp.zowi.marco.zowiapp.adapters.GuidedGameListAdapter;
import zowiapp.zowi.marco.zowiapp.utils.Layout;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activities);

        String activityType = getIntent().getExtras().getString("type");

        String[] unitsTitles, activitiesTitles, activitiesImages;
        if (activityType != null) {
            unitsTitles = activityType.equals(CommonConstants.GUIDED) ? getResources().getStringArray(R.array.units_titles) : null;
            activitiesTitles = activityType.equals(CommonConstants.GUIDED) ? getResources().getStringArray(R.array.activities_titles) : getResources().getStringArray(R.array.free_game_activities_titles);
            activitiesImages = activityType.equals(CommonConstants.GUIDED) ? getResources().getStringArray(R.array.activities_images) : getResources().getStringArray(R.array.free_game_activities_images);

            ListView container = (ListView) findViewById(R.id.list_activities);
            if (container != null) {
                if (activityType.equals(CommonConstants.GUIDED))
                    container.setAdapter(new GuidedGameListAdapter(this, unitsTitles, activitiesTitles, activitiesImages));
                else
                    container.setAdapter(new FreeGameListAdapter(this, activitiesTitles, activitiesImages));
            }
        }

        final LinearLayout guidedGameScroller = (LinearLayout) findViewById(R.id.game_scroller);
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
