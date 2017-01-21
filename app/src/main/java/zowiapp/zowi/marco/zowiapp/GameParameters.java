package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GameParameters extends AppCompatActivity {

    private LayoutInflater inflater;
    private GameParameters context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity_template);

        context = this;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle parameter = getIntent().getExtras();
        String activityTitle = parameter.getString("activityTitle");
        int activityNumber = parameter.getInt("activityNumber");

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

        setEvents(gridActivityTemplate);

        contentContainer.addView(gridActivityTemplate);
    }

    private void setEvents(View gridActivityTemplate) {
        Button controls = (Button) gridActivityTemplate.findViewById(R.id.controls);

        controls.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float x = event.getX();
                        float y = event.getY();
                        float rightCorner = v.getWidth();
                        float center = rightCorner/2;
                        /* The control's width is 500px, and the diameter of the circumference is 150px */
                        double circumferenceRadius = rightCorner/6.66;

                        double distanceToCenter = Math.sqrt(Math.pow(x-center, 2)+Math.pow(y-center, 2));

                        /* If coordinates are inside the circumference */
                        if (distanceToCenter < circumferenceRadius) {
                            Toast.makeText(context, "Go!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            /* Lines ecuations are used to determine where the user has touched the control */
                            if (x>y) {
                                if (y < (rightCorner-x)) {
                                    Toast.makeText(context, "Zona 1", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(context, "Zona 2", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                if (y > (rightCorner-x)) {
                                    Toast.makeText(context, "Zona 3", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(context, "Zona 4", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
