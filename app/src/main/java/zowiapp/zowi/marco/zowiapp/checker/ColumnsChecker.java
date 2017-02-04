package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

/**
 * Created by Marco on 03/02/2017.
 */
public class ColumnsChecker {

    public void check(GameParameters gameParameters, String chosenColumn, String correctColumn) {
        if (chosenColumn.equals(correctColumn)) {
            // Send data to Zowi
            Log.i("ColumnsChecker", "Bien");
        }
        else {
            Log.i("ColumnsChecker", "Mal");
        }
    }

}
