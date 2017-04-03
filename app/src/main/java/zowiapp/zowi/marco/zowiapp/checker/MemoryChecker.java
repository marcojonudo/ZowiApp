package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/02/2017.
 */
public class MemoryChecker {

    public boolean check(GameParameters gameParameters, int firstImageId, int secondImageId, int firstPosition, int secondPosition) {
        if ((firstImageId == secondImageId) && (firstPosition != secondPosition)) {
            Toast.makeText(gameParameters, "Bien", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
