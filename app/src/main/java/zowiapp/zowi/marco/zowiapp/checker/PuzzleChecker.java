package zowiapp.zowi.marco.zowiapp.checker;

import android.graphics.Point;

import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.activities.PuzzleActivity;
import zowiapp.zowi.marco.zowiapp.utils.AsyncTaskHandler;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

public class PuzzleChecker extends CheckerTemplate {

    private PuzzleActivity puzzleActivity;
    private Point[] piecesCoordinates, correction;

    public PuzzleChecker(PuzzleActivity puzzleActivity) {
        this.puzzleActivity = puzzleActivity;
    }

    public void check(Point[] piecesCoordinates, Point[] correction) {
        this.piecesCoordinates = piecesCoordinates;
        this.correction = correction;

        new AsyncTaskHandler(this, ActivityType.PUZZLE).execute(ZowiActions.ZOWI_CHECKS_ANSWERS);
    }

    public void checkAnswers() {
        double distance;
        boolean correctAnswer = true;
        for (int i=0; i<piecesCoordinates.length; i++) {
            distance = Math.sqrt(Math.pow(piecesCoordinates[i].x-correction[i].x, 2) + Math.pow(piecesCoordinates[i].y-correction[i].y, 2));
            if (distance > PuzzleConstants.DISTANCE_LIMIT) {
                correctAnswer = false;
            }
        }
        if (correctAnswer) {
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            puzzleActivity.registerCorrectAnswer(true);
        }
        else {
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
            puzzleActivity.registerCorrectAnswer(false);
        }
    }

}
