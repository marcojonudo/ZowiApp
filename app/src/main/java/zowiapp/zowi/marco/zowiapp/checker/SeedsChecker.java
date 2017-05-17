package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.MainActivity;
import zowiapp.zowi.marco.zowiapp.ZowiSocket;

/**
 * Created by Marco on 03/02/2017.
 */
public class SeedsChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, String imageCategory, String containerCategory) {
        if (imageCategory.equals(containerCategory)) {
            Toast.makeText(gameParameters, "Bien", Toast.LENGTH_SHORT).show();
            sendDataToZowi(CORRECT_ANSWER_COMMAND);
            return true;
        }
        else {
            Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
            sendDataToZowi(WRONG_ANSWER_COMMAND);
            return false;
        }
    }

    @Override
    public void sendDataToZowi(String command) {
        ZowiSocket.sendCommand(command);
    }

}
