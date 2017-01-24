package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import zowiapp.zowi.marco.zowiapp.ActivityConstants.*;

public class GameParameters extends AppCompatActivity {

    private LayoutInflater inflater;
    private GameParameters context;
    private ActivityType activityType;
    float startX, startY, upperLimit = 0;

    private int[][] coordinates, baCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity_template);

        context = this;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle parameter = getIntent().getExtras();
        String activityTitle = parameter.getString(CommonConstants.INTENT_PARAMETER_TITLE);
        int activityNumber = parameter.getInt(CommonConstants.INTENT_PARAMETER_NUMBER);

        /* Array with all the details needed to create the activity */
        String[] activitiesDetails = getResources().getStringArray(R.array.activities_details);

        chooseActivityType(activityTitle, activityNumber, activitiesDetails);
    }

    private void chooseActivityType(String activityTitle, int activityNumber, String[] activitiesDetails) {
        try {
            String json = activitiesDetails[activityNumber];
            JSONObject activityDetails = new JSONObject(json);
            String activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);

            /* Set the title and the description of the activity */
            setTitleDescription(activityTitle, activityDescription);

            activityType = ActivityType.valueOf(activityDetails.getString(CommonConstants.JSON_PARAMETER_TYPE));

            switch (activityType) {
                /* Grid activity */
                case GRID:
                    int gridSize = activityDetails.getInt(GridConstants.JSON_PARAMETER_GRIDSIZE);
                    JSONArray jsonCells = activityDetails.getJSONArray(GridConstants.JSON_PARAMETER_CELLS);
                    JSONArray jsonImages = activityDetails.getJSONArray(GridConstants.JSON_PARAMETER_IMAGES);
                    int[] cells = new int[jsonCells.length()];
                    String[] images = new String[jsonImages.length()];

                    for (int i=0; i<jsonCells.length(); i++) {
                        cells[i] = jsonCells.getInt(i);
                        images[i] = jsonImages.getString(i);
                    }
                    generateGridActivity(gridSize, cells, images);
                    break;
                /* Before and after activity */
                case COLUMNS:
                    String leftTitle = activityDetails.getString(ColumnsConstants.JSON_PARAMETER_LEFTTITLE);
                    String rightTitle = activityDetails.getString(ColumnsConstants.JSON_PARAMETER_RIGHTTITLE);
                    JSONArray jsonBAImages = activityDetails.getJSONArray(ColumnsConstants.JSON_PARAMETER_IMAGES);
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

    protected void hola() {

    }

    private void generateBAActivity(final String[] images, String leftTitle, String rightTitle) {
        final RelativeLayout contentContainer = (RelativeLayout) findViewById(R.id.content_container);
        LinearLayout beforeAfterActivityTemplate = (LinearLayout) inflater.inflate(R.layout.before_after_activity_template, contentContainer, false);
        contentContainer.addView(beforeAfterActivityTemplate);

        TextView lTitle = (TextView) findViewById(R.id.left_title);
        TextView rTitle = (TextView) findViewById(R.id.right_title);
        lTitle.setText(leftTitle);
        rTitle.setText(rightTitle);

        LayoutListener layoutListener = new LayoutListener(activityType, contentContainer, context, images);

        contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    contentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    RelativeLayout imagesContainer = (RelativeLayout) findViewById(R.id.images_container);

                    /* baCoordinates will contain the images' coordinates, and the ones from the corners of the
                       columns */
                    baCoordinates = new int[images.length+ColumnsConstants.COLUMNS_INCREMENT][CommonConstants.AXIS_NUMBER];

                    /* The first coordinates are the ones from the element in the center */
                    int centerX = imagesContainer.getLeft() + imagesContainer.getWidth()/2;
                    int centerY = imagesContainer.getTop() + imagesContainer.getHeight()/2;
                    baCoordinates[0][0] = centerX - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
                    baCoordinates[0][1] = centerY - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;

                    /* Place the other images on the corners of a square */
                    /* 'distance' is a intermediate distance between the center and the limits of the container */
                    int distance = (imagesContainer.getWidth()/6)*2;
                    for (int i=1; i<5; i++) {
                        int x = (int) Math.round(centerX + distance*Math.cos(ColumnsConstants.CIRCUMFERENCE_INITIAL_POS*(Math.PI/180) + ColumnsConstants.CIRCUMFERENCE_INCREMENT*(Math.PI/180)*(i-1)));
                        int y = (int) Math.round(centerY + distance*Math.sin(ColumnsConstants.CIRCUMFERENCE_INITIAL_POS*(Math.PI/180) + ColumnsConstants.CIRCUMFERENCE_INCREMENT*(Math.PI/180)*(i-1)));

                        baCoordinates[i][0] = x - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
                        baCoordinates[i][1] = y - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
                    }

                    View leftColumn = findViewById(R.id.left_column);
                    View rightColumn = findViewById(R.id.right_column);

                    /* Coordinates of the corners of the columns */
                    baCoordinates[baCoordinates.length-ColumnsConstants.LEFT_COL_INDEX_ADJUSTMENT][0] = leftColumn.getLeft() + leftColumn.getWidth();
                    baCoordinates[baCoordinates.length-ColumnsConstants.LEFT_COL_INDEX_ADJUSTMENT][1] = leftColumn.getTop();
                    baCoordinates[baCoordinates.length-ColumnsConstants.RIGHT_COL_INDEX_ADJUSTMENT][0] = leftColumn.getLeft() + leftColumn.getWidth() + imagesContainer.getWidth();
                    baCoordinates[baCoordinates.length-ColumnsConstants.RIGHT_COL_INDEX_ADJUSTMENT][1] = rightColumn.getTop();

                    placeBAImages(contentContainer, images);
                }
            });
    }

    protected void getElementsCoordinates() {
        switch (activityType) {
            case COLUMNS:
                RelativeLayout imagesContainer = (RelativeLayout) findViewById(R.id.images_container);

                    /* baCoordinates will contain the images' coordinates, and the ones from the corners of the
                       columns */
                baCoordinates = new int[images.length+ColumnsConstants.COLUMNS_INCREMENT][CommonConstants.AXIS_NUMBER];

                    /* The first coordinates are the ones from the element in the center */
                int centerX = imagesContainer.getLeft() + imagesContainer.getWidth()/2;
                int centerY = imagesContainer.getTop() + imagesContainer.getHeight()/2;
                baCoordinates[0][0] = centerX - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
                baCoordinates[0][1] = centerY - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;

                    /* Place the other images on the corners of a square */
                    /* 'distance' is a intermediate distance between the center and the limits of the container */
                int distance = (imagesContainer.getWidth()/6)*2;
                for (int i=1; i<5; i++) {
                    int x = (int) Math.round(centerX + distance*Math.cos(ColumnsConstants.CIRCUMFERENCE_INITIAL_POS*(Math.PI/180) + ColumnsConstants.CIRCUMFERENCE_INCREMENT*(Math.PI/180)*(i-1)));
                    int y = (int) Math.round(centerY + distance*Math.sin(ColumnsConstants.CIRCUMFERENCE_INITIAL_POS*(Math.PI/180) + ColumnsConstants.CIRCUMFERENCE_INCREMENT*(Math.PI/180)*(i-1)));

                    baCoordinates[i][0] = x - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
                    baCoordinates[i][1] = y - ColumnsConstants.COLUMNS_TRANSLATION_TO_CENTER;
                }

                View leftColumn = findViewById(R.id.left_column);
                View rightColumn = findViewById(R.id.right_column);

                    /* Coordinates of the corners of the columns */
                baCoordinates[baCoordinates.length-ColumnsConstants.LEFT_COL_INDEX_ADJUSTMENT][0] = leftColumn.getLeft() + leftColumn.getWidth();
                baCoordinates[baCoordinates.length-ColumnsConstants.LEFT_COL_INDEX_ADJUSTMENT][1] = leftColumn.getTop();
                baCoordinates[baCoordinates.length-ColumnsConstants.RIGHT_COL_INDEX_ADJUSTMENT][0] = leftColumn.getLeft() + leftColumn.getWidth() + imagesContainer.getWidth();
                baCoordinates[baCoordinates.length-ColumnsConstants.RIGHT_COL_INDEX_ADJUSTMENT][1] = rightColumn.getTop();

                placeBAImages(contentContainer, images);
        }
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ColumnsConstants.COLUMNS_IMAGE_WIDTH_PX, ColumnsConstants.COLUMNS_IMAGE_WIDTH_PX);
        image.setLayoutParams(layoutParams);
        image.setX(x);
        image.setY(y);
        image.setTag(i);

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
                        int leftColumnIndex = baCoordinates.length-ColumnsConstants.LEFT_COL_INDEX_ADJUSTMENT;
                        int rightColumnIndex = baCoordinates.length-ColumnsConstants.RIGHT_COL_INDEX_ADJUSTMENT;
                        float topLeftCornerX = view.getX();
                        float topRightCornerX = view.getX() + view.getWidth();
                        float topLeftCornerY = view.getY();
                        float bottomLeftCornerY = view.getY() + view.getHeight();

                        if ((topLeftCornerX < baCoordinates[leftColumnIndex][0])
                                &&(bottomLeftCornerY > baCoordinates[leftColumnIndex][1])) {
                            Toast.makeText(context, "Columna izquierda", Toast.LENGTH_SHORT).show();

                            /* Actions to do if the image is not completely inside the column */
                            /* The image is on the corner */
                            if ((topRightCornerX > baCoordinates[leftColumnIndex][0])
                                    &&(topLeftCornerY < baCoordinates[leftColumnIndex][1])) {
                                view.setX(baCoordinates[leftColumnIndex][0]-view.getWidth());
                                view.setY(baCoordinates[leftColumnIndex][1]);
                            }
                            /* The image is on the top edge */
                            else if (topLeftCornerY < baCoordinates[leftColumnIndex][1]) {
                                view.setY(baCoordinates[leftColumnIndex][1]);
                            }
                            /* The image is on the right edge */
                            else if (topRightCornerX > baCoordinates[leftColumnIndex][0]) {
                                view.setX(baCoordinates[leftColumnIndex][0]-view.getWidth());
                            }
                        }
                        /* The view is placed inside the right column */
                        else if ((topRightCornerX > baCoordinates[rightColumnIndex][0])
                                &&((view.getY()+view.getHeight()) > baCoordinates[rightColumnIndex][1])) {
                            Toast.makeText(context, "Columna derecha", Toast.LENGTH_SHORT).show();

                            /* The image is on the corner */
                            if ((topLeftCornerX < baCoordinates[rightColumnIndex][0])
                                    &&(topLeftCornerY < baCoordinates[rightColumnIndex][1])) {
                                view.setX(baCoordinates[rightColumnIndex][0]);
                                view.setY(baCoordinates[rightColumnIndex][1]);
                            }
                            /* The image is on the top edge */
                            else if (topLeftCornerY < baCoordinates[rightColumnIndex][1]) {
                                view.setY(baCoordinates[rightColumnIndex][1]);
                            }
                            /* The image is on the left edge */
                            else if (topLeftCornerX < baCoordinates[rightColumnIndex][0]) {
                                view.setX(baCoordinates[rightColumnIndex][0]);
                            }
                        }
                        /* The element goes back to the original position */
                        else {
                            view.setX(baCoordinates[(int)view.getTag()][0]);
                            view.setY(baCoordinates[(int)view.getTag()][1]);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        /* The distance of the element to the start point is calculated when the user
                           moves it */
                        float distanceX = event.getRawX() - startX;
                        float distanceY = event.getRawY() - startY;

                        /* Mechanism to avoid the element to move behind the title and description
                           It is only moved when it is in 'contentContainer' */
                        if (event.getRawY() > upperLimit) {
                            view.setX(baCoordinates[(int)view.getTag()][0]+distanceX);
                            view.setY(baCoordinates[(int)view.getTag()][1]+distanceY);

                            if (view.getY()<=0) {
                                upperLimit = event.getRawY();
                            }
                        }
                        else {
                            view.setX(baCoordinates[(int)view.getTag()][0]+distanceX);
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
                coordinates = new int[GridConstants.COORDINATES_3X3_LENGTH][CommonConstants.AXIS_NUMBER];
                break;
            /* 4x4 */
            case 2:
                inflater.inflate(R.layout.grid_4x4_template, grid, true);
                coordinates = new int[GridConstants.COORDINATES_4X4_LENGTH][CommonConstants.AXIS_NUMBER];
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
                    coordinates[i+1][0] = coordinates[0][0] + (((i%rows)*2 + 1)*halfCell) - GridConstants.GRID_TRANSLATION_TO_CENTER;
                    coordinates[i+1][1] = coordinates[0][1] + (((i/rows)*2 + 1)*halfCell) - GridConstants.GRID_TRANSLATION_TO_CENTER;
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
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(GridConstants.GRID_IMAGE_WIDTH_PX, GridConstants.GRID_IMAGE_WIDTH_PX);
            image.setLayoutParams(layoutParams);
            image.setX(coordinates[cells[i]][0]);
            image.setY(coordinates[cells[i]][1]);

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
                        /* The control's width is 500px, and the diameter of the circumference is 150px (500/(150/2))*/
                        double circumferenceRadius = rightCorner/GridConstants.CONTROL_RADIUS_FACTOR;

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

    interface CreationStep {
        void setEvents();
        void setLayoutListener(RelativeLayout relativeLayout);
    }

    private CreationStep[] creationSteps = new CreationStep[] {
            new CreationStep() {
                @Override
                public void setEvents() {
                    Toast.makeText(context, "setEvents", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void setLayoutListener(RelativeLayout relativeLayout) {
                    Toast.makeText(context, "setEvents", Toast.LENGTH_SHORT).show();
                }
            }
    };

    private void create() {
        for (int i=0; i<creationSteps.length; i++) {
            creationSteps[i].setEvents();
        }
    }
}
