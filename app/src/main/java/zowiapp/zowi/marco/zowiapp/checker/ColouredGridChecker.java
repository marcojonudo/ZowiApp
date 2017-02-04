package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

/**
 * Created by Marco on 03/02/2017.
 */
public class ColouredGridChecker {

    public void check(GameParameters gameParameters, int[] colouredCellsNumber, String stringColor, String result) {
        Colors color = Colors.valueOf(stringColor);
        int numberResult = Integer.valueOf(result);

        switch (color) {
            case RED:
                if (colouredCellsNumber[0] == numberResult) {
                    // Send OK to Zowi
                    Log.i("ColouredGridChecker", "Rojo bien");
                }
                else {
                    // Send WRONG to Zowi
                    Log.i("ColouredGridChecker", "Rojo bien");
                }
                break;
            case BLUE:
                if (colouredCellsNumber[1] == numberResult) {
                    // Send OK to Zowi
                    Log.i("ColouredGridChecker", "Azul bien");
                }
                else {
                    // Send WRONG to Zowi
                    Log.i("ColouredGridChecker", "Azul bien");
                }
                break;
            case GREEN:
                if (colouredCellsNumber[2] == numberResult) {
                    // Send OK to Zowi
                    Log.i("ColouredGridChecker", "Verde bien");
                }
                else {
                    // Send WRONG to Zowi
                    Log.i("ColouredGridChecker", "Verde bien");
                }
                break;
        }
    }

    public enum Colors {
        RED, BLUE, GREEN
    }

}
