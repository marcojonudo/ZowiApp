package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class GameParameters extends AppCompatActivity {

    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity_template);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle parameter = getIntent().getExtras();
        String activityTitle = parameter.getString("activityTitle");
        int activityNumber = parameter.getInt("activityNumber");

        Toast.makeText(this, String.valueOf(activityNumber), Toast.LENGTH_SHORT).show();

        String[] activitiesDetails = getResources().getStringArray(R.array.activities_details);

        try {
            String json = activitiesDetails[activityNumber];
            JSONObject activityDetails = new JSONObject(json);
            String activityDescription = activityDetails.getString("description");

            setTitleDescription(activityTitle, activityDescription);

            switch (activityDetails.getInt("type")) {
                case 1:
                    int gridSize = activityDetails.getInt("gridSize");
                    JSONArray jsonCoordinates = activityDetails.getJSONArray("coordinates");
                    int[] coordinates = new int[jsonCoordinates.length()];
                    for (int i=0; i<jsonCoordinates.length(); i++) {
                        coordinates[i] = (int) jsonCoordinates.get(i);
                    }

                    generateGridActivity(gridSize, coordinates);
                    break;
                default:
                    break;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTitleDescription(String activityTitle, String activityDescription) {
        TextView title = (TextView) findViewById(R.id.activity_title);
        TextView description = (TextView) findViewById(R.id.activity_description);

        title.setText(activityTitle);
        description.setText(activityDescription);
    }

    private void generateGridActivity(int gridSize, int[] coordinates) {
        RelativeLayout contentContainer = (RelativeLayout) findViewById(R.id.content_container);
        RelativeLayout gridActivityTemplate = (RelativeLayout) inflater.inflate(R.layout.grid_activity_template, contentContainer, false);

        switch (gridSize) {
            /* 3x3 */
            case 1:
                /* The grid is added automatically to gridActivityTemplate because the third parameter is 'true' */
                inflater.inflate(R.layout.grid_3x3_template, gridActivityTemplate, true);
                break;
            /* 4x4 */
            case 2:
                inflater.inflate(R.layout.grid_4x4_template, gridActivityTemplate, true);
                break;
            default:
                break;
        }

        contentContainer.addView(gridActivityTemplate);
    }
}
