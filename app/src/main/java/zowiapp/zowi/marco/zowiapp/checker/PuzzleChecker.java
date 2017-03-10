package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;
/**
 * Created by Marco on 03/02/2017.
 */
public class PuzzleChecker {

    public boolean check(GameParameters gameParameters, int[] piecesCoordinates, int[] puzzleCoordinates) {
        double distanceToPoint = Math.sqrt(Math.pow(piecesCoordinates[0]-puzzleCoordinates[0], 2) + Math.pow(piecesCoordinates[1]-puzzleCoordinates[1], 2));

        if (distanceToPoint < PuzzleConstants.DISTANCE_LIMIT) {
            Toast.makeText(gameParameters, "Bien!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(gameParameters, "Mal, eres un inútil!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
