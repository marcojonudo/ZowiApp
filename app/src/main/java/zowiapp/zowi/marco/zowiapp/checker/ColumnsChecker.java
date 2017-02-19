package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

/**
 * Created by Marco on 03/02/2017.
 */
public class ColumnsChecker {

    public void check(GameParameters gameParameters, String chosenColumn, String correctColumn) {
        if (chosenColumn.equals(correctColumn)) {
            // Send data to Zowi
            Toast.makeText(gameParameters, "Bien", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
        }
    }

}
