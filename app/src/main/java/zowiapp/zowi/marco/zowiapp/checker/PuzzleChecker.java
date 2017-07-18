package zowiapp.zowi.marco.zowiapp.checker;

import android.graphics.Point;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

public class PuzzleChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, float x, float y, Point puzzleCoordinates) {
        double distanceToPoint = Math.sqrt(Math.pow(x-puzzleCoordinates.x, 2) + Math.pow(y-puzzleCoordinates.y, 2));

        if (distanceToPoint < PuzzleConstants.DISTANCE_LIMIT) {
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            return true;
        }
        else {
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
            return false;
        }
    }

}
