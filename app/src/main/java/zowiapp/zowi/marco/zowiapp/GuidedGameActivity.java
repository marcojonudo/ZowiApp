package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewOverlay;
import android.widget.LinearLayout;
import android.widget.ListView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;

public class GuidedGameActivity extends AppCompatActivity {

    private ListView container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guided_game);

        String[] unitsTitles = getResources().getStringArray(R.array.units_titles);
        String[] activitiesTitles = getResources().getStringArray(R.array.activities_titles);
        String[] activitiesImages = getResources().getStringArray(R.array.activities_images);

        container = (ListView) findViewById(R.id.list_guided_activities);
        container.setAdapter(new CustomListAdapter(this, unitsTitles, activitiesTitles, activitiesImages));

        final LinearLayout guidedGameScroller = (LinearLayout) findViewById(R.id.guided_game_scroller);
        if (guidedGameScroller != null) {
            final Drawable smallZowi = ContextCompat.getDrawable(this, R.drawable.overlay_off);
            final ViewOverlay overlay = guidedGameScroller.getOverlay();

            guidedGameScroller.post(new Runnable() {
                @Override
                public void run() {
                    smallZowi.setBounds(guidedGameScroller.getWidth()-guidedGameScroller.getWidth()/CommonConstants.OVERLAY_HORIZONTAL_RATIO, guidedGameScroller.getHeight()-guidedGameScroller.getWidth()/CommonConstants.OVERLAY_HORIZONTAL_RATIO, guidedGameScroller.getWidth(), guidedGameScroller.getHeight());
                    overlay.add(smallZowi);
                }
            });
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
