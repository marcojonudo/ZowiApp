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
    private String[] unitsTitles, activitiesTitles, activitiesImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guided_game);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        unitsTitles = getResources().getStringArray(R.array.units_titles);
        activitiesTitles = getResources().getStringArray(R.array.activities_titles);
        activitiesImages = getResources().getStringArray(R.array.activities_images);

        String[] unitsTitles = getResources().getStringArray(R.array.units_titles);
        String[] activitiesTitles = getResources().getStringArray(R.array.activities_titles);
        String[] activitiesImages = getResources().getStringArray(R.array.activities_images);

        container = (ListView) findViewById(R.id.list_guided_activities);
        container.setAdapter(new CustomListAdapter(this, unitsTitles, activitiesTitles, activitiesImages));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
