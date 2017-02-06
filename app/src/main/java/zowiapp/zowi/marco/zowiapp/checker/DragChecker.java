package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/02/2017.
 */
public class DragChecker {

    public void check(GameParameters gameParameters, String imageCategory, String containerCategory) {
        if (imageCategory.equals(containerCategory)) {
            Toast.makeText(gameParameters, "Bien", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
        }
    }

}
