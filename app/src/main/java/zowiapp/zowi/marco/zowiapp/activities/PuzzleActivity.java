package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;
import zowiapp.zowi.marco.zowiapp.checker.PuzzleChecker;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class PuzzleActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private PuzzleChecker puzzleChecker;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private String image;
    private String[] piecesImages;
    private int shape;
    private int[][] puzzleCoordinates, piecesCoordinates;
    private int imageWidth, imageHeight;
    private float piecesScalingFactor;
    private int layoutListenerStep;
    float startX, startY, upperLimit = 0;
    int puzzleContainerHeight;

    public PuzzleActivity(final GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutListenerStep = 0;
        puzzleChecker = new PuzzleChecker();

        getParameters();

        new CountDownTimer(15000, 15000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                puzzleChecker.check(gameParameters, piecesImages, puzzleCoordinates, piecesCoordinates);
            }
        }.start();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            image = activityDetails.getString(PuzzleConstants.JSON_PARAMETER_IMAGE);
            shape = activityDetails.getInt(PuzzleConstants.JSON_PARAMETER_SHAPE);
            int piecesNumber = activityDetails.getInt(PuzzleConstants.JSON_PARAMETER_PIECESNUMBER);
            piecesImages = new String[piecesNumber];
            puzzleCoordinates = new int[piecesNumber][CommonConstants.AXIS_NUMBER];
            piecesCoordinates = new int[piecesNumber][CommonConstants.AXIS_NUMBER];

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

                /* The reference point is the upper left corner of the square. All the coordinates are
                   calculated based on it, multiplying by the factors */
                int[] upperLeftCorner = {puzzleContainer.getLeft(), puzzleContainer.getTop()};
                int puzzleWidth = puzzleContainer.getWidth();
                int puzzleHeight = puzzleContainer.getHeight();

                /* Depending on the shape of the puzzle, the coordinates are stored */
                double[][] coordinatesFactors = null;
                switch (shape) {
                    case 1:
                        coordinatesFactors = PuzzleConstants.SHAPES_COORDINATES_FACTORS[0];
                        break;
                    case 2:
                        coordinatesFactors = PuzzleConstants.SHAPES_COORDINATES_FACTORS[1];
                        break;
                    case 3:
                        coordinatesFactors = PuzzleConstants.SHAPES_COORDINATES_FACTORS[2];
                        break;
                    default:
                        break;
                }

                /* This loop stores the coordinates of the puzzle */
                for (int i=0; i<piecesCoordinates.length; i++) {
                    puzzleCoordinates[i][0] = (int)(upperLeftCorner[0] + (puzzleWidth*coordinatesFactors[i][0]));
                    puzzleCoordinates[i][1] = (int)(upperLeftCorner[1] + (puzzleHeight*coordinatesFactors[i][1]));

                    View v = new View(gameParameters);
                    ViewGroup.LayoutParams l = new ViewGroup.LayoutParams(15, 15);
                    v.setLayoutParams(l);
                    v.setX(puzzleCoordinates[i][0]);
                    v.setY(puzzleCoordinates[i][1]);
                    v.setBackground(ContextCompat.getDrawable(gameParameters, R.drawable.black));
                    contentContainer.addView(v);

                    /* The first child is 'puzzleContainer', the rest of them the pieces */
                    ImageView pieceImage = (ImageView) contentContainer.getChildAt(i+1);
                    piecesCoordinates[i][0] = pieceImage.getLeft();
                    piecesCoordinates[i][1] = pieceImage.getTop();
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
        image.setTag(i);

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
                float originalWidth = (view.getWidth()/scaleFactor)/PuzzleConstants.PIECES_TO_PUZZLE[(int)view.getTag()][0];
                float originalHeight = (view.getHeight()/scaleFactor)/PuzzleConstants.PIECES_TO_PUZZLE[(int)view.getTag()][1];
                ViewGroup.LayoutParams l = view.getLayoutParams();
                l.height = (int)originalHeight;
                l.width = (int)originalWidth;
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
                    view.setX(piecesCoordinates[(int)view.getTag()][0]+distanceX);
                    view.setY(piecesCoordinates[(int)view.getTag()][1]+distanceY);

                    if (view.getY()<=0) {
                        upperLimit = event.getRawY();
                    }
                }
                else {
                    view.setX(piecesCoordinates[(int)view.getTag()][0]+distanceX);
                }
                break;
            case MotionEvent.ACTION_UP:
                upperLimit = 0;

                piecesCoordinates[(int)view.getTag()][0] = (int)view.getX();
                piecesCoordinates[(int)view.getTag()][1] = (int)view.getY();

                /* It is better not to place the elements automatically. Instead of that, in the correction
                   stage it will be checked if the pieces' coordinates are OK or not */
//                float viewX = view.getX();
//                float viewY = view.getY();
//                /* -1 because the images' tag starts at 1, not at 0 */
//                int index = (int)view.getTag() - 1;
//
//                double distanceToPoint = Math.sqrt(Math.pow(viewX-puzzleCoordinates[index][0], 2) + Math.pow(viewY-puzzleCoordinates[index][1], 2));
//
//                if (distanceToPoint < PuzzleConstants.DISTANCE_LIMIT) {
//                    view.setX(puzzleCoordinates[(int)view.getTag()-1][0]);
//                    view.setY(puzzleCoordinates[(int)view.getTag()-1][1]);
//                }
//                else {
//                    view.setX(piecesCoordinates[(int)view.getTag()-1][0]);
//                    view.setY(piecesCoordinates[(int)view.getTag()-1][1]);
//                }

                break;
            default:
                break;
        }
    }

}
