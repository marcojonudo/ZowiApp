package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;
/**
 * Created by Marco on 03/02/2017.
 */
public class PuzzleChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, float x, float y, int[] puzzleCoordinates) {
        double distanceToPoint = Math.sqrt(Math.pow(x-puzzleCoordinates[0], 2) + Math.pow(y-puzzleCoordinates[1], 2));

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
