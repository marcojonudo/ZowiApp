package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

/**
 * Created by Marco on 24/01/2017.
 */
public class PuzzleActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private PuzzleChecker puzzleChecker;
    private ImagesHandler imagesHandler;
    private String[][][] piecesImages;
    private int randomShapeIndex, puzzleContainerSide;
    private int[][] puzzleCoordinates, piecesCoordinates, piecesDimensions, correction;
    private float[][] scaleFactorsToPuzzle;
    private float distanceToLeft, distanceToTop;
    private int[] dragLimits;

    public PuzzleActivity(final GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        puzzleChecker = new PuzzleChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.PUZZLE);

        getParameters();
    }

    public void setCorrection(int[][] correction) {
        this.correction = correction;
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
            dragLimits = new int[4];

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

        if (contentContainer != null) {
            dragLimits[0] = 0;
            dragLimits[1] = 0;
            dragLimits[2] = contentContainer.getRight();
            dragLimits[3] = contentContainer.getBottom();
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }

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

            imagesHandler.loadPuzzleImages(contentContainer, piecesImages[randomShapeIndex][randomImageIndex], PuzzleConstants.PIECES_NUMBER, piecesCoordinates, piecesDimensions, scaleFactorsToPuzzle, randomShapeIndex, puzzleContainerSide);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "puzzleContainer");
        }
    }

    void processTouchEvent(View view, MotionEvent event) {
        float left, right, top, bottom;
        float scaleFactorToPuzzle = -1;

        LinearLayout headerText = (LinearLayout) gameParameters.findViewById(R.id.header_text);
        int headerTextHeight = 0;
        if (headerText != null) {
            headerTextHeight = headerText.getHeight();
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "headerText");
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                distanceToLeft = view.getX() - event.getRawX();
                distanceToTop = headerTextHeight + view.getY() - event.getRawY();

                int index = (int) view.getTag();

                if (scaleFactorsToPuzzle[(int)view.getTag()][0] != 0) {
                    scaleFactorToPuzzle = scaleFactorsToPuzzle[index][0];
                }
                else {
                    scaleFactorToPuzzle = scaleFactorsToPuzzle[index][1];
                }

                Animations.scaleAnimation(view, scaleFactorToPuzzle, PuzzleConstants.SCALE_ANIMATION_PIVOTS, index);

                /* Bring the view to the front in order to avoid strange effects when dragging, moving the piece
                   begind the others */
                view.bringToFront();
                break;
            case MotionEvent.ACTION_MOVE:
                left = event.getRawX() + distanceToLeft;
                right = event.getRawX() + (view.getWidth()+ distanceToLeft);
                top = event.getRawY() + distanceToTop - headerTextHeight;
                bottom = event.getRawY() + (view.getHeight()+ distanceToTop);

                if ((left <= dragLimits[0] || right >= dragLimits[2])) {
                    if ((top > dragLimits[1]) && (bottom < dragLimits[3])) {
                        view.setY(top);
                    }
                }
                else if ((top <= dragLimits[1]) || (bottom >= dragLimits[3])) {
                    if ((left > dragLimits[0] && right < dragLimits[2])) {
                        view.setX(left);
                    }
                }
                else {
                    view.setX(left);
                    view.setY(top);
                }
                Log.i("View position", view.getX() + ", " + view.getY());
                break;
            case MotionEvent.ACTION_UP:
                index = (int) view.getTag();

                boolean correctAnswer = puzzleChecker.check(gameParameters, view.getX(), view.getY(), correction[index]);

                if (correctAnswer) {
                    Animations.translateAnimation(view, correction, index);
                }
                else {
                    Animations.translateAnimation(view, piecesCoordinates, index);
                    Animations.scaleAnimation(view, scaleFactorToPuzzle, PuzzleConstants.SCALE_ANIMATION_PIVOTS, index);
                }

                break;
            default:
                break;
        }
    }

}
