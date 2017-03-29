package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;
import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/02/2017.
 */
public class ColumnsChecker {

    public boolean check(GameParameters gameParameters, String chosenColumn, String correctColumn) {
        if (chosenColumn.equals(correctColumn)) {
            // Send data to Zowi
            Toast.makeText(gameParameters, "Bien", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
