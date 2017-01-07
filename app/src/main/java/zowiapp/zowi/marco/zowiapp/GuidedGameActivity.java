package zowiapp.zowi.marco.zowiapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class GuidedGameActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guided_game);

        listView = (ListView) findViewById(R.id.list_guided_activities);
        listView.setAdapter(new CustomListAdapter(this));
    }

}
