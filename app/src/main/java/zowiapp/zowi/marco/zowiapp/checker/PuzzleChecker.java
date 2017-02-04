package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;
/**
 * Created by Marco on 03/02/2017.
 */
public class PuzzleChecker {

    public void check(GameParameters gameParameters, String[] piecesImages, int[][] puzzleCoordinates, int[][] piecesCoordinates) {
        for (int i=0; i<piecesImages.length; i++) {
            /* The number of the image is got to make sure that the correct image is place in the correct place */
            String[] piecesImageSplit = piecesImages[i].split("_");
            int imageNumber = Integer.parseInt(piecesImageSplit[piecesImageSplit.length-1]);

            double distanceToPoint = Math.sqrt(Math.pow(piecesCoordinates[i][0]-puzzleCoordinates[i][0], 2) + Math.pow(piecesCoordinates[i][1]-puzzleCoordinates[i][1], 2));

            if ((imageNumber == (i+1))&&(distanceToPoint < PuzzleConstants.DISTANCE_LIMIT)) {
                Log.i("PuzzleChecker", i + ": bien");
            }
            else {
                Log.i("PuzzleChecker", i + ": mal");
            }
        }

    }

}
