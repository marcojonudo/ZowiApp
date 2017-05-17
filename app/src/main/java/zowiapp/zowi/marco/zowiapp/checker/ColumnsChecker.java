package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.ZowiSocket;
import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/02/2017.
 */
public class ColumnsChecker extends CheckerTemplate{

    private static final String correctAnwerCommand = "C 1";
    private static final String wrongAnswerCommand = "C 2";

    public boolean check(GameParameters gameParameters, String chosenColumn, String correctColumn) {
        if (chosenColumn.equals(correctColumn)) {
            Toast.makeText(gameParameters, "Bien", Toast.LENGTH_SHORT).show();
            sendDataToZowi(correctAnwerCommand);
            return true;
        }
        else {
            Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
            sendDataToZowi(wrongAnswerCommand);
            return false;
        }
    }

    @Override
    public void sendDataToZowi(String command) {
        ZowiSocket.sendCommand(command);
    }

}
