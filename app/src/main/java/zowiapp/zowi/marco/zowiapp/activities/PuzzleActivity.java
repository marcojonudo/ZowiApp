package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;
import zowiapp.zowi.marco.zowiapp.checker.PuzzleChecker;
import zowiapp.zowi.marco.zowiapp.error.NullElement;
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
    private String[][][] piecesImages;
    private int randomShapeIndex, puzzleContainerSide;
    private int[][] puzzleCoordinates, piecesCoordinates, piecesDimensions, correction;
    private float[][] scaleFactorsToPuzzle;
    private float startX, startY, upperLimit = 0;
    private boolean[] alreadyResized;

    public PuzzleActivity(final GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        puzzleChecker = new PuzzleChecker();

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(PuzzleConstants.JSON_PARAMETER_IMAGES);
            piecesImages = new String[jsonImages.length()][][];
            puzzleCoordinates = new int[PuzzleConstants.PIECES_NUMBER][CommonConstants.AXIS_NUMBER];
            piecesCoordinates = new int[PuzzleConstants.PIECES_NUMBER][CommonConstants.AXIS_NUMBER];
            piecesDimensions = new int[PuzzleConstants.PIECES_NUMBER][CommonConstants.AXIS_NUMBER];
            scaleFactorsToPuzzle = new float[PuzzleConstants.PIECES_NUMBER][CommonConstants.AXIS_NUMBER];
            correction = new int[PuzzleConstants.PIECES_NUMBER][CommonConstants.AXIS_NUMBER];
            alreadyResized = new boolean[PuzzleConstants.PIECES_NUMBER];

            /* The name of the pieces images is the name of the image followed by '_number' */
            for (int i=0; i<piecesImages.length; i++) {
                JSONArray jsonShapeImages = jsonImages.getJSONArray(i);
                piecesImages[i] = new String[jsonShapeImages.length()][PuzzleConstants.PIECES_NUMBER];

                for (int j=0; j<piecesImages[i].length; j++) {
                    String image = jsonShapeImages.getString(j);
                    for (int h=0; h<PuzzleConstants.PIECES_NUMBER; h++) {
                        piecesImages[i][j][h] = image + "_" + (h+1);
                    }
                }
            }
            for (int i=0; i<alreadyResized.length; i++) {
                alreadyResized[i] = false;
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
        ConstraintLayout puzzleActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.puzzle_activity_template, contentContainer, false);

        if (contentContainer != null) {
            contentContainer.addView(puzzleActivityTemplate);
            LayoutListener layoutListener = new LayoutListener(PuzzleConstants.PUZZLE_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        ConstraintLayout puzzleContainer = (ConstraintLayout) gameParameters.findViewById(R.id.puzzle_image_container);

        if (puzzleContainer != null) {
            /* The reference point is the upper left corner of the square. All the coordinates are
               calculated based on it, multiplying by the factors */
            int left = puzzleContainer.getLeft();
            int top = puzzleContainer.getTop();

            /* 'puzzleContainer' has a 1:1 aspect ratio */
            puzzleContainerSide = puzzleContainer.getWidth();

            /* The length of piecesImages is the number of possible shapes */
            randomShapeIndex = new Random().nextInt(piecesImages.length);
            /* Depending on the shape of the puzzle, the coordinates factors are stored */
            double[][] coordinatesFactors = PuzzleConstants.SHAPES_COORDINATES_FACTORS[randomShapeIndex];

            /* This loop stores the coordinates of the puzzle */
            for (int i=0; i<piecesCoordinates.length; i++) {
                puzzleCoordinates[i][0] = (int)(left + (puzzleContainerSide*coordinatesFactors[i][0]));
                puzzleCoordinates[i][1] = (int)(top + (puzzleContainerSide*coordinatesFactors[i][1]));

                /* The first child is 'puzzleContainer', the rest of them the pieces */
                String pieceContainerName = "piece_" + (i+1);
                View imagePiece = gameParameters.findViewById(gameParameters.getResources().getIdentifier(pieceContainerName, "id", gameParameters.getPackageName()));

                ConstraintLayout piecesContainer;
                if (i<3) {
                    piecesContainer = (ConstraintLayout) gameParameters.findViewById(R.id.left_pieces_container);
                }
                else {
                    piecesContainer = (ConstraintLayout) gameParameters.findViewById(R.id.right_pieces_container);
                }
                if ((imagePiece != null)&&(piecesContainer != null)) {
                    piecesCoordinates[i][0] = piecesContainer.getLeft() + imagePiece.getLeft() + imagePiece.getWidth()/2;
                    piecesCoordinates[i][1] = piecesContainer.getTop() + imagePiece.getTop() + imagePiece.getHeight()/2;
                    piecesDimensions[i][0] = imagePiece.getWidth();
                    piecesDimensions[i][1] = imagePiece.getHeight();
                }
                else {
                    new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "pieces");
                }
            }

            /* The length of piecesImages[i] is the number of possible images */
            int randomImageIndex = new Random().nextInt(piecesImages[0].length);

            placeImages(contentContainer, piecesImages[randomShapeIndex][randomImageIndex]);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "puzzleContainer");
        }
    }

    private void placeImages(RelativeLayout contentContainer, String[] images) {
        int[] occurrences = new int[images.length];
        for (int i=0; i<occurrences.length; i++) {
            occurrences[i] = 0;
        }

        for (int i=0; i<images.length; i++) {
            int randomIndex = new Random().nextInt(images.length);

            while (occurrences[randomIndex] == 1) {
                randomIndex = new Random().nextInt(occurrences.length);
            }
            occurrences[randomIndex] = 1;

            /* With the correction array there are no problems with different indexes between the positions and the pieces*/
            correction[i] = puzzleCoordinates[randomIndex];

            placeImage(contentContainer, images[randomIndex], randomIndex, i);
        }

    }

    private void placeImage(RelativeLayout container, String imageName, int randomIndex, int i) {
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));

        Drawable drawable = image.getDrawable();
        float scaleFactor, scaleFactorToPuzzle;
        int width, height;
        if (piecesDimensions[i][0] < piecesDimensions[i][1]) {
            scaleFactor = (float)piecesDimensions[i][0]/(float)drawable.getIntrinsicWidth();
            width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
            height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
            scaleFactorToPuzzle = ((float)puzzleContainerSide*(float)PuzzleConstants.PIECES_TO_PUZZLE[randomShapeIndex][randomIndex][0])/(float)width;
            scaleFactorsToPuzzle[i][0] = scaleFactorToPuzzle;
        }
        else {
            scaleFactor = (float)piecesDimensions[i][1]/(float)drawable.getIntrinsicHeight();
            width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
            height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
            scaleFactorToPuzzle = ((float)puzzleContainerSide*(float)PuzzleConstants.PIECES_TO_PUZZLE[randomShapeIndex][randomIndex][1])/(float)height;
            scaleFactorsToPuzzle[i][1] = scaleFactorToPuzzle;
        }

        piecesDimensions[i][0] = width;
        piecesDimensions[i][1] = height;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        image.setLayoutParams(layoutParams);
        piecesCoordinates[i][0] = piecesCoordinates[i][0] - width/2;
        piecesCoordinates[i][1] = piecesCoordinates[i][1] - height/2;
        image.setX(piecesCoordinates[i][0]);
        image.setY(piecesCoordinates[i][1]);
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

                int index = (int)view.getTag();
                if (!alreadyResized[index]) {
                    float scaleFactorToPuzzle;

                    if (scaleFactorsToPuzzle[(int)view.getTag()][0] != 0) {
                        scaleFactorToPuzzle = scaleFactorsToPuzzle[index][0];
                    }
                    else {
                        scaleFactorToPuzzle = scaleFactorsToPuzzle[index][1];
                    }
                    int puzzlePieceWidth = (int)(view.getWidth() * scaleFactorToPuzzle);
                    int puzzlePieceHeight = (int)(view.getHeight() * scaleFactorToPuzzle);
                    ViewGroup.LayoutParams l = view.getLayoutParams();
                    l.width = puzzlePieceWidth;
                    l.height = puzzlePieceHeight;

                    alreadyResized[index] = true;
                }

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

                index = (int)view.getTag();
                piecesCoordinates[index][0] = (int)view.getX();
                piecesCoordinates[index][1] = (int)view.getY();

                puzzleChecker.check(gameParameters, piecesCoordinates[index], correction[index]);

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
