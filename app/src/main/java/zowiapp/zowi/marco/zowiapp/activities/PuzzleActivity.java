package zowiapp.zowi.marco.zowiapp.activities;

import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;
import zowiapp.zowi.marco.zowiapp.utils.Layout;

public class PuzzleActivity extends ActivityTemplate {

    static int imagesCounter;
    private String[][][] piecesImages;
    private Point[] puzzleCoordinates, piecesDimensions;
    Point[] piecesCoordinates, correction;
    float[] scaleFactorsToPuzzle;

    public PuzzleActivity(final GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new PuzzleChecker(this);
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.PUZZLE);

        getParameters();
    }

    public void setCorrection(Point[] correction) {
        this.correction = correction;
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(PuzzleConstants.JSON_PARAMETER_IMAGES);
            piecesImages = new String[jsonImages.length()][][];
            puzzleCoordinates = Functions.createEmptyPointArray(PuzzleConstants.PIECES_NUMBER);
            piecesCoordinates = Functions.createEmptyPointArray(PuzzleConstants.PIECES_NUMBER);
            piecesDimensions = Functions.createEmptyPointArray(PuzzleConstants.PIECES_NUMBER);
            scaleFactorsToPuzzle = new float[PuzzleConstants.PIECES_NUMBER];
            correction = Functions.createEmptyPointArray(PuzzleConstants.PIECES_NUMBER);
            dragLimits = new int[CommonConstants.DRAG_LIMITS_SIZE];

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
        ConstraintLayout puzzleActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.guided_puzzle_activity_template, contentContainer, false);

        if (contentContainer != null) {
            contentContainer.addView(puzzleActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(PuzzleConstants.PUZZLE_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        ConstraintLayout puzzleContainer = (ConstraintLayout) gameParameters.findViewById(R.id.puzzle_image_container);

        if (contentContainer != null)
            setDragLimits(contentContainer);
        else
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");

        if (puzzleContainer != null) {
            /* The reference point is the upper left corner of the square. All the coordinates are
               calculated based on it, multiplying by the factors */
            int left = puzzleContainer.getLeft();
            int top = puzzleContainer.getTop();

            /* 'puzzleContainer' has a 1:1 aspect ratio */
            int puzzleContainerSide = puzzleContainer.getWidth();

            /* The length of piecesImages is the number of possible shapes */
            int randomShapeIndex = new Random().nextInt(piecesImages.length);
            /* Depending on the shape of the puzzle, the coordinates factors are stored */
            double[][] coordinatesFactors = PuzzleConstants.PUZZLE_SHAPES_COORDINATES_FACTORS[randomShapeIndex];

            /* This loop stores the coordinates of the puzzle */
            for (int i=0; i<piecesCoordinates.length; i++) {
                puzzleCoordinates[i].set((int)(left + (puzzleContainerSide*coordinatesFactors[i][0])), (int)(top + (puzzleContainerSide*coordinatesFactors[i][1])));

                /* The first child is 'puzzleContainer', the rest of them the pieces */
                String pieceContainerName = "piece_" + (i+1);
                View imagePiece = gameParameters.findViewById(gameParameters.getResources().getIdentifier(pieceContainerName, "id", gameParameters.getPackageName()));

                ConstraintLayout piecesContainer = (ConstraintLayout) gameParameters.findViewById(i<3 ? R.id.left_pieces_container : R.id.right_pieces_container);

                if ((imagePiece != null) && (piecesContainer != null)) {
                    piecesCoordinates[i].set(piecesContainer.getLeft() + imagePiece.getLeft() + imagePiece.getWidth()/2, piecesContainer.getTop() + imagePiece.getTop() + imagePiece.getHeight()/2);
                    piecesDimensions[i].set(imagePiece.getWidth(), imagePiece.getHeight());
                }
                else {
                    new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "pieces");
                }
            }

            /* The length of piecesImages[i] is the number of possible images */
            int randomImageIndex = new Random().nextInt(piecesImages[0].length);

            imagesHandler.loadPuzzleImages(contentContainer, piecesImages[randomShapeIndex][randomImageIndex], PuzzleConstants.PIECES_NUMBER,
                    puzzleCoordinates, piecesCoordinates, piecesDimensions, scaleFactorsToPuzzle, PuzzleConstants.PIECES_TO_PUZZLE[randomShapeIndex],
                    PuzzleConstants.CORRECTION_SHAPES_COORDINATES_FACTORS[randomShapeIndex], puzzleContainerSide);

            createCheckButton(contentContainer, true);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "puzzleContainer");
        }
    }

    private void createCheckButton(ViewGroup contentContainer, boolean guidedActivity) {
        Button checkButton = Layout.createFloatingCheckButton(gameParameters, inflater, contentContainer, guidedActivity);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = gameParameters.getResources().getString(R.string.zowi_checks_dialog_text);
                Layout.showGenericAlertDialog(gameParameters, true, text);
                ((PuzzleChecker) checker).check(piecesCoordinates, correction);
            }
        });
    }

    public void registerCorrectAnswer(boolean correctAnswer) {
        checkFinishActivity(ActivityType.PUZZLE, correctAnswer, 1, true);
    }

    void processTouchEvent(View view, MotionEvent event) {
        handleEvents(ActivityType.PUZZLE, view, event, null, null);
    }

}
