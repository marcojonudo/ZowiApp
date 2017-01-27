package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class PuzzleActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private String image;
    private String[] piecesImages;
    private int shape;
    private int[][] coordinates;
    private int imageWidth, imageHeight;
    private float piecesScalingFactor;
    private int layoutListenerStep;
    float startX, startY, upperLimit = 0;
    int puzzleContainerHeight;

    public PuzzleActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutListenerStep = 0;

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            image = activityDetails.getString(PuzzleConstants.JSON_PARAMETER_IMAGE);
            shape = activityDetails.getInt(PuzzleConstants.JSON_PARAMETER_SHAPE);
            int piecesNumber = activityDetails.getInt(PuzzleConstants.JSON_PARAMETER_PIECESNUMBER);
            piecesImages = new String[piecesNumber];
            coordinates = new int[piecesNumber*2][CommonConstants.AXIS_NUMBER];

            /* The name of the pieces images is the name of the image followed by '_number' */
            for (int i=0; i<piecesImages.length; i++) {
                piecesImages[i] = image + "_" + (i+1);
            }

            generateLayout();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void generateLayout() {
        setTitleDescription(gameParameters, activityTitle, activityDescription);

        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);

        if (contentContainer != null) {
            LayoutListener layoutListener = new LayoutListener(PuzzleConstants.PUZZLE_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout puzzleContainer, contentContainer;
        switch (layoutListenerStep) {
            case 0:
                /* The layout has to load to know the dimensions of the content container, that is why
                the LayoutListener is used */
                contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
                RelativeLayout puzzleActivityTemplate = (RelativeLayout) inflater.inflate(R.layout.puzzle_activity_template, contentContainer, false);
                puzzleContainer = (RelativeLayout) puzzleActivityTemplate.findViewById(R.id.puzzle_container);
                int containerHeight = contentContainer.getHeight();
                puzzleContainerHeight = containerHeight-PuzzleConstants.CONTENT_CONTAINER_MARGIN;

                /* Get the height and the width of the image from resource file */
                BitmapFactory.Options dimensions = new BitmapFactory.Options();
                dimensions.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(gameParameters.getResources(), gameParameters.getResources().getIdentifier(image, "drawable", gameParameters.getPackageName()), dimensions);
                imageHeight = dimensions.outHeight;
                imageWidth =  dimensions.outWidth;

                /* If the image height is bigger than the container's, it is scaled to fit the screen */
                float scalingFactor;
                scalingFactor = (float)imageHeight/(float)puzzleContainerHeight;

                imageHeight /= scalingFactor;
                imageWidth /= scalingFactor;

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageWidth, imageHeight);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                puzzleContainer.setLayoutParams(layoutParams);

                contentContainer.addView(puzzleActivityTemplate);
                layoutListenerStep = 1;

                placeImages(contentContainer, piecesImages, containerHeight);

                LayoutListener layoutListener = new LayoutListener(PuzzleConstants.PUZZLE_TYPE, contentContainer, this);
                contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                break;
            case 1:
                /* Once the puzzle container is loaded, the coordinates are obtained */
                contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
                puzzleContainer = (RelativeLayout) gameParameters.findViewById(R.id.puzzle_container);

                /* This loop stores the coordinates of the puzzle container in the first 5 position,
                   and the ones of the pieces in the last 5 */
                for (int i=0; i<coordinates.length/2; i++) {
                    switch (i) {
                        /* Upper left corner coordinates - 1st piece */
                        case 0:
                            coordinates[i][0] = puzzleContainer.getLeft();
                            coordinates[i][1] = puzzleContainer.getTop();
                            break;
                        /* Upper left corner coordinates - 2nd piece */
                        case 1:
                            coordinates[i][0] = puzzleContainer.getLeft();
                            coordinates[i][1] = puzzleContainer.getTop();
                            break;
                        /* Left, middle height - 3rd piece */
                        case 2:
                            coordinates[i][0] = puzzleContainer.getLeft();
                            coordinates[i][1] = puzzleContainer.getTop() + puzzleContainer.getHeight()/2;
                            break;
                        /* Middle width, top - 4th piece */
                        case 3:
                            coordinates[i][0] = puzzleContainer.getLeft() + puzzleContainer.getWidth()/2;
                            coordinates[i][1] = puzzleContainer.getTop();
                            break;
                        /* Middle - 5th piece */
                        case 4:
                            coordinates[i][0] = puzzleContainer.getLeft() + puzzleContainer.getWidth()/2;
                            coordinates[i][1] = puzzleContainer.getTop() + puzzleContainer.getHeight()/2;
                            break;
                        default:
                            break;
                    }
                    /* The first child is 'puzzleContainer', the rest of them the pieces */
                    ImageView pieceImage = (ImageView) contentContainer.getChildAt(i+1);
                    coordinates[coordinates.length-(i+1)][0] = pieceImage.getLeft();
                    coordinates[coordinates.length-(i+1)][1] = pieceImage.getTop();
                }
                break;
            default:
                break;
        }
    }

    private void placeImages(RelativeLayout contentContainer, String[] images, int containerHeight) {
        int[][] alignments = {{RelativeLayout.ALIGN_PARENT_START, RelativeLayout.ALIGN_PARENT_TOP},
                                {RelativeLayout.ALIGN_PARENT_START, RelativeLayout.ALIGN_PARENT_BOTTOM},
                                {RelativeLayout.ALIGN_PARENT_END, RelativeLayout.ALIGN_PARENT_TOP},
                                {RelativeLayout.ALIGN_PARENT_END, RelativeLayout.ALIGN_PARENT_BOTTOM},
                                {RelativeLayout.ALIGN_PARENT_START, RelativeLayout.CENTER_VERTICAL},
                                {RelativeLayout.ALIGN_PARENT_END, RelativeLayout.CENTER_VERTICAL}};
        int[] alignmentsRepetition = {0, 0, 0, 0, 0, 0};

        piecesScalingFactor = imageHeight/(float)(containerHeight/3);

        /* The position of the pieces is random because of the use of 'alignments' */
        for (int i=0; i<images.length; i++) {
            int alignmentsIndex = new Random().nextInt(alignments.length);
            while (alignmentsRepetition[alignmentsIndex] == 1) {
                alignmentsIndex = new Random().nextInt(alignments.length);
            }
            alignmentsRepetition[alignmentsIndex] = 1;
            placeImage(contentContainer, images[i], alignments[alignmentsIndex], i);
        }

    }

    private void placeImage(RelativeLayout container, String imageName, int[] alignments, int i) {
        /* The pieces are made smaller to fit the screen */
        float pieceWidth = imageWidth/piecesScalingFactor;
        float pieceHeight = imageHeight/piecesScalingFactor;
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)pieceWidth, (int)pieceHeight);
        layoutParams.addRule(alignments[0], RelativeLayout.TRUE);
        layoutParams.addRule(alignments[1], RelativeLayout.TRUE);
        image.setLayoutParams(layoutParams);
        /* Pieces coordinates start at index coordinates.length-1 */
        image.setTag(i+1);

        container.addView(image);

        TouchListener touchListener = new TouchListener(PuzzleConstants.PUZZLE_TYPE, this);
        image.setOnTouchListener(touchListener);
    }

    protected void processTouchEvent(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /* Values used to calculate de distance to move the element */
                startX = event.getRawX();
                startY = event.getRawY();

                /* Return the image to the original size, in order to fit the puzzle */
                float scaleFactor = (float)view.getHeight()/(float)puzzleContainerHeight;
                float originalWidth = (view.getWidth()/scaleFactor)/PuzzleConstants.PIECES_TO_PUZZLE[(int)view.getTag()-1][0];
                float originalHeight = (view.getHeight()/scaleFactor)/PuzzleConstants.PIECES_TO_PUZZLE[(int)view.getTag()-1][1];
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)originalWidth, (int)originalHeight);
                view.setLayoutParams(layoutParams);
                /* Bring the view to the front in order to avoid strange effects when dragging, moving the piece
                   begind the others */
                view.bringToFront();
                break;
            case MotionEvent.ACTION_MOVE:
                /* The distance of the element to the start point is calculated when the user
                   moves it */
                float distanceX = event.getRawX() - startX;
                float distanceY = event.getRawY() - startY;

                /* Mechanism to avoid the element to move behind the title and description
                   It is only moved when it is in 'contentContainer' */
                if (event.getRawY() > upperLimit) {
                    view.setX(coordinates[coordinates.length-(int)view.getTag()][0]+distanceX);
                    view.setY(coordinates[coordinates.length-(int)view.getTag()][1]+distanceY);

                    if (view.getY()<=0) {
                        upperLimit = event.getRawY();
                    }
                }
                else {
                    view.setX(coordinates[coordinates.length-(int)view.getTag()][0]+distanceX);
                }
                break;
            case MotionEvent.ACTION_UP:
                float viewX = view.getX();
                float viewY = view.getY();
                /* -1 because the images' tag starts at 1, not at 0 */
                int index = (int)view.getTag() - 1;

                double distanceToPoint = Math.sqrt(Math.pow(viewX-coordinates[index][0], 2) + Math.pow(viewY-coordinates[index][1], 2));

                if (distanceToPoint < PuzzleConstants.DISTANCE_LIMIT) {
                    view.setX(coordinates[(int)view.getTag()-1][0]);
                    view.setY(coordinates[(int)view.getTag()-1][1]);
                }
                else {
                    view.setX(coordinates[coordinates.length-(int)view.getTag()][0]);
                    view.setY(coordinates[coordinates.length-(int)view.getTag()][1]);
                }

                break;
            default:
                break;
        }
    }

}
