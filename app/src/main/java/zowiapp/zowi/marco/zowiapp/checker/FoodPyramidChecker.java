package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.ZowiSocket;
import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/02/2017.
 */
public class FoodPyramidChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, String imageCategory, String containerCategory) {
        if (imageCategory.equals(containerCategory)) {
            Toast.makeText(gameParameters, "Bien", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void sendDataToZowi(String command) {
        ZowiSocket.sendCommand(command);
    }

}
