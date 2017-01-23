package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GameParameters extends AppCompatActivity {

    private LayoutInflater inflater;
    private GameParameters context;
    float startX, startY, upperLimit = 0;

    private int[][] coordinates, baCoordinates;

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

            /* Set the title and the description of the activity */
            setTitleDescription(activityTitle, activityDescription);

            switch (activityDetails.getInt("type")) {
                /* Grid activity */
                case 1:
                    int gridSize = activityDetails.getInt("gridSize");
                    JSONArray jsonCells = activityDetails.getJSONArray("cells");
                    JSONArray jsonImages = activityDetails.getJSONArray("images");
                    int[] cells = new int[jsonCells.length()];
                    String[] images = new String[jsonImages.length()];

                    for (int i=0; i<jsonCells.length(); i++) {
                        cells[i] = jsonCells.getInt(i);
                        images[i] = jsonImages.getString(i);
                    }

                    generateGridActivity(gridSize, cells, images);
                    break;
                /* Before and after activity */
                case 2:
                    String leftTitle = activityDetails.getString("leftTitle");
                    String rightTitle = activityDetails.getString("rightTitle");
                    JSONArray jsonBAImages = activityDetails.getJSONArray("images");
                    String[] BAImages = new String[jsonBAImages.length()];

                    for (int i=0; i<jsonBAImages.length(); i++) {
                        BAImages[i] = jsonBAImages.getString(i);
                    }

                    generateBAActivity(BAImages, leftTitle, rightTitle);
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

    private void generateBAActivity(final String[] images, String leftTitle, String rightTitle) {
        final RelativeLayout contentContainer = (RelativeLayout) findViewById(R.id.content_container);
        final LinearLayout beforeAfterActivityTemplate = (LinearLayout) inflater.inflate(R.layout.before_after_activity_template, contentContainer, false);

        TextView lTitle = (TextView) beforeAfterActivityTemplate.findViewById(R.id.left_title);
        TextView rTitle = (TextView) beforeAfterActivityTemplate.findViewById(R.id.right_title);
        lTitle.setText(leftTitle);
        rTitle.setText(rightTitle);

        beforeAfterActivityTemplate.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    beforeAfterActivityTemplate.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    RelativeLayout imagesContainer = (RelativeLayout) findViewById(R.id.images_container);

                    /* baCoordinates will contain the images' coordinates, and the ones from the corners of the
                       columns */
                    baCoordinates = new int[images.length+2][2];

                    /* The first coordinates are the ones from the element in the center */
                    int centerX = imagesContainer.getLeft() + imagesContainer.getWidth()/2;
                    int centerY = imagesContainer.getTop() + imagesContainer.getHeight()/2;
                    baCoordinates[0][0] = centerX;
                    baCoordinates[0][1] = centerY;

                    /* Place the other images on the corners of a square */
                    /* 'distance' is a intermediate distance between the center and the limits of the container */
                    int distance = (imagesContainer.getWidth()/6)*2;
                    for (int i=1; i<5; i++) {
                        int x = (int) Math.round(centerX + distance*Math.cos(45*(Math.PI/180) + 90*(Math.PI/180)*(i-1)));
                        int y = (int) Math.round(centerY + distance*Math.sin(45*(Math.PI/180) + 90*(Math.PI/180)*(i-1)));

                        baCoordinates[i][0] = x;
                        baCoordinates[i][1] = y;
                    }

                    View leftColumn = beforeAfterActivityTemplate.findViewById(R.id.left_column);
                    View rightColumn = beforeAfterActivityTemplate.findViewById(R.id.right_column);

                    /* Coordinates of the corners of the columns */
                    baCoordinates[baCoordinates.length-2][0] = leftColumn.getLeft() + leftColumn.getWidth();
                    baCoordinates[baCoordinates.length-2][1] = leftColumn.getTop();
                    baCoordinates[baCoordinates.length-1][0] = leftColumn.getLeft() + leftColumn.getWidth() + imagesContainer.getWidth();
                    baCoordinates[baCoordinates.length-1][1] = rightColumn.getTop();

                    placeBAImages(contentContainer, images);
                }
            });

        contentContainer.addView(beforeAfterActivityTemplate);
    }

    private void placeBAImages(RelativeLayout contentContainer, String[] images) {
        /* Place the first image in the center of imagesContainer */
        for (int i=0; i<images.length; i++) {
            placeImage(contentContainer, images[i], baCoordinates[i][0], baCoordinates[i][1], i);
        }

    }

    private void placeImage(RelativeLayout container, String imageName, int x, int y, int i) {
        ImageView image = new ImageView(this);
        image.setImageResource(getResources().getIdentifier(imageName, "drawable", getPackageName()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        image.setLayoutParams(layoutParams);
        image.setX(x-75);
        image.setY(y-75);
        image.setTag(i);

        float a = image.getX();
        float b = image.getY();

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        /* Values used to calculate de distance to move the element */
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        /* The view is placed inside the left column */
                        if ((view.getX() < baCoordinates[baCoordinates.length-2][0])
                                &&((view.getY()+view.getHeight()) > baCoordinates[baCoordinates.length-2][1])) {
                            Toast.makeText(context, "Columna izquierda", Toast.LENGTH_SHORT).show();
                        }
                        /* The view is placed inside the right column */
                        else if (((view.getX()+view.getWidth()) > baCoordinates[baCoordinates.length-1][0])
                                &&((view.getY()+view.getHeight()) > baCoordinates[baCoordinates.length-1][1])) {
                            Toast.makeText(context, "Columna derecha", Toast.LENGTH_SHORT).show();
                        }
                        /* The element goes back to the original position */
                        else {
                            view.setX(baCoordinates[(int)view.getTag()][0]-75);
                            view.setY(baCoordinates[(int)view.getTag()][1]-75);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        /* The distance of the element to the start point is calculated when the user
                           moves it */
                        float distanceX = event.getRawX() - startX;
                        float distanceY = event.getRawY() - startY;

                        /* Mechanism to avoid the element to move behind the title and description */
                        /* It is only moved when it is in 'contentContainer' */
                        if (event.getRawY() > upperLimit) {
                            view.setX(baCoordinates[(int)view.getTag()][0]-75+distanceX);
                            view.setY(baCoordinates[(int)view.getTag()][1]-75+distanceY);

                            if (view.getY()<=0) {
                                upperLimit = event.getRawY();
                            }
                        }
                        else {
                            view.setX(baCoordinates[(int)view.getTag()][0]-75+distanceX);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        /*image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AnimationSet set = new AnimationSet(true);

                TranslateAnimation translateAnimation = new TranslateAnimation(0, 300, 0, 0);
                translateAnimation.setDuration(1000);

                set.addAnimation(translateAnimation);

                set.setFillAfter(true);

                v.startAnimation(set);
            }
        });*/

        container.addView(image);
    }

    private void generateGridActivity(int gridSize, int[] cells, String[] images) {
        RelativeLayout contentContainer = (RelativeLayout) findViewById(R.id.content_container);
        RelativeLayout gridActivityTemplate = (RelativeLayout) inflater.inflate(R.layout.grid_activity_template, contentContainer, false);
        GridLayout grid = (GridLayout) gridActivityTemplate.findViewById(R.id.grid);

        switch (gridSize) {
            /* 3x3 */
            case 1:
                /* The grid is added automatically to 'grid' because the third parameter is 'true' */
                inflater.inflate(R.layout.grid_3x3_template, grid, true);
                coordinates = new int[10][2];
                break;
            /* 4x4 */
            case 2:
                inflater.inflate(R.layout.grid_4x4_template, grid, true);
                coordinates = new int[17][2];
                break;
            default:
                break;
        }

        /* Set onTouchListener on 'controls' */
        setEvents(gridActivityTemplate);
        /* Listener for saving the coordinates of the cells */
        setLayoutListener(gridActivityTemplate, gridSize, cells, images);

        contentContainer.addView(gridActivityTemplate);
    }

    private void setLayoutListener(final RelativeLayout gridActivityTemplate, final int gridSize, final int[] cells, final String[] images) {
        GridLayout grid = (GridLayout) gridActivityTemplate.findViewById(R.id.grid);

        /* This observer saves the coordinates of the cells, once they are loaded into the layout */
        grid.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                GridLayout grid = (GridLayout) findViewById(R.id.grid);
                /* 'grid' contains the 3x3 or 4x4 grid */
                GridLayout gameGrid = (GridLayout) grid.getChildAt(0);

                coordinates[0][0] = grid.getLeft();
                coordinates[0][1] = grid.getTop();

                /* Values used to calculate the center of the cells */
                int halfCell = 0, rows = 0;
                switch (gridSize) {
                    case 1:
                        halfCell = grid.getWidth()/6;
                        rows = 3;
                        break;
                    case 2:
                        halfCell = grid.getWidth()/8;
                        rows = 4;
                        break;
                    default:
                        break;
                }

                /* Cell center coordinates are calculated based on the upper left corner of the grid */
                for (int i=0; i<gameGrid.getChildCount(); i++) {
                    coordinates[i+1][0] = coordinates[0][0] + (((i%rows)*2 + 1)*halfCell);
                    coordinates[i+1][1] = coordinates[0][1] + (((i/rows)*2 + 1)*halfCell);
                }
                gameGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                placeElements(gridActivityTemplate, cells, images);
            }
        });
    }

    private void placeElements(RelativeLayout gridActivityTemplate, int[] cells, String[] images) {

        /* 'cells' contains a number between 1 and 9 or 16 that indicates the cells that will contain an image */
        /* 'images' contains the name of the resources */
        for (int i=0; i<cells.length; i++) {
            ImageView image = new ImageView(this);
            image.setImageResource(getResources().getIdentifier(images[i], "drawable", getPackageName()));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
            image.setLayoutParams(layoutParams);
            image.setX(coordinates[cells[i]][0]-75);
            image.setY(coordinates[cells[i]][1]-75);

            gridActivityTemplate.addView(image);
        }
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
